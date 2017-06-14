(ns flappy-bird-demo.handlers
  (:require
    [flappy-bird-demo.db :as flappy.db]
    [cljs.spec.alpha :as spec]
    [re-frame.core :as re-frame :refer [reg-event-db]]))

(defn validate-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (spec/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (spec/explain-str a-spec db)) {}))))

;; Event handlers change state, that's their job. But what happens if there's
;; a bug which corrupts app state in some subtle way? This interceptor is run after
;; each event handler has finished, and it checks app-db against a spec.  This
;; helps us detect event handler bugs early.
(def check-spec-interceptor (re-frame/after (partial validate-and-throw :flappy-bird-demo.db/db)))

(def flappy-interceptors
  [check-spec-interceptor                         ;; ensure the spec is still valid
   (when ^boolean js/goog.DEBUG re-frame/debug)]) ;; log events to console 

(defn get-current-timestamp []
  (.getTime (js/Date.)))

;; -- Handlers --------

(defn floor [x] (.floor js/Math x))

(defn translate [start-pos vel time]
  (floor (+ start-pos (* time vel))))

(defn curr-pillar-pos [{:keys [horiz-vel]} cur-time {:keys [pos-x start-time] }]
  (translate pos-x horiz-vel (- cur-time start-time)))

(defn in-pillar? [{:keys [flappy-x flappy-width pillar-width]} {:keys [cur-x]}]
  (and (>= (+ flappy-x flappy-width)
           cur-x)
       (< flappy-x (+ cur-x pillar-width))))

(defn in-pillar-gap? [{:keys [pillar-gap flappy-height]} flappy-y {:keys [gap-top]}]
  (and (< gap-top flappy-y)
       (> (+ gap-top pillar-gap)
          (+ flappy-y flappy-height))))

(defn bottom-collision? [{:keys [bottom-y flappy-height]} flappy-y]
  (>= flappy-y (- bottom-y flappy-height)))

(defn collision? [{:keys [pillar-list flappy-y options] :as db}]
  (some #(or (and (in-pillar? options %)
                  (not (in-pillar-gap? options flappy-y %)))
             (bottom-collision? options flappy-y)) pillar-list))

(defn stop-on-collision [db]
  (if (collision? db)
    (assoc db :timer-running false)
    db))

(defn new-pillar [{:keys [pillar-gap bottom-y]} cur-time pos-x k]
  {:key k
   :start-time cur-time
   :pos-x      pos-x
   :cur-x      pos-x
   :gap-top    (+ 60 (rand-int (- bottom-y 120 pillar-gap)))})

(defn update-pillars [{:keys [pillar-list cur-time options] :as db}]
  (let [pillars-with-pos (map #(assoc % :cur-x (curr-pillar-pos options cur-time %)) pillar-list)
        {:keys [pillar-width pillar-spacing]} options
        pillars-in-world (sort-by
                          :cur-x
                          (filter #(> (:cur-x %) (- pillar-width)) pillars-with-pos))
        new-pillars (if (< (count pillars-in-world) 3)
                      (conj pillars-in-world
                            (new-pillar options cur-time
                                        (+ pillar-spacing
                                           (:cur-x (last pillars-in-world)))
                                        (->> pillar-list (map :key) (reduce max) inc)))
                      pillars-in-world)]
    (assoc db :pillar-list new-pillars)))

(defn calculate-flappy-y-as-sine-wave [db]
  (let [start-y (get-in db [:options :start-y])]
    (+ start-y
       (* 80 (.sin js/Math (/ (:time-delta db)
                              200))))))

(defn calculate-flappy-y-from-jumps [{:keys [time-delta initial-vel flappy-y options] :as db}]
  (let [{:keys [gravity bottom-y flappy-height]} options
        cur-vel (- initial-vel (* time-delta gravity))
        new-y   (- flappy-y cur-vel)
        bottom-limit (- bottom-y flappy-height)]
    (if (> new-y bottom-limit)
      bottom-limit
      new-y)))

(defn update-flappy [{:keys [jump-count] :as db}]
  (assoc db :flappy-y (if (pos? jump-count)
                        (calculate-flappy-y-from-jumps db)
                        (calculate-flappy-y-as-sine-wave db)))) 

(defn update-score [{:keys [cur-time start-time options] :as db}]
  (let [{:keys [horiz-vel pillar-spacing]} options
        score (- (.abs js/Math (floor (/ (- (* (- cur-time start-time) horiz-vel) 544)
                               pillar-spacing)))
                 4)]
    (assoc db :score (if (neg? score) 0 score))))

(defn update-border [{:keys [cur-time options] :as db}]
  (-> db
      (assoc :border-pos (mod (translate 0 (:horiz-vel options) cur-time) 23))))

(defn pillar-offset [{:keys [bottom-y pillar-gap]} {:keys [gap-top] :as p}]
  (assoc p
    :upper-height gap-top
    :lower-height (- bottom-y gap-top pillar-gap)))

(defn update-pillar-offsets [{:keys [options] :as db}]
  (-> db
      (update :pillar-list
              (fn [pillar-list]
                (map (partial pillar-offset options) pillar-list)))))

(defn time-update [timestamp db]
  (-> db
      (assoc :cur-time timestamp
             :time-delta (- timestamp (:flappy-start-time db)))
      update-flappy
      update-pillars
      stop-on-collision  
      update-score
      update-border
      update-pillar-offsets
      ))

(defn register-handlers! []

  (reg-event-db
    :initialize-db
    flappy-interceptors
    (fn [db]
      (if (empty? db)
        flappy.db/default-db
        (assoc db :options (:options flappy.db/default-db)))))

  (reg-event-db
    :start-game
    flappy-interceptors
    (fn [db]
      (let [cur-time (get-current-timestamp)]
        (-> db
            (update-in [:pillar-list] (fn [pls] (map #(assoc % :start-time cur-time) pls)))
            (assoc
              :start-time cur-time
              :flappy-start-time cur-time
              :timer-running true)))))

  (reg-event-db
    :jump
    flappy-interceptors
    (fn [db]
      (-> db
          (update :jump-count inc)
          (assoc :flappy-start-time (get-in db [:cur-time])
                 :initial-vel (get-in db [:options :jump-vel])))))

  (reg-event-db
    :tick
    [check-spec-interceptor] ;; skip debug interceptor, too noisy
    (fn [db]
      (if (and (:timer-running db)
               (not (:paused db)))
        (time-update (get-current-timestamp) db)
        db)))

  (reg-event-db
    :toggle-pause
    flappy-interceptors
    (fn [db]
      (update db :paused (fnil not false)))))
