(ns flappy-bird-demo.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(defn floor [x] (.floor js/Math x))

(defn register-subs! []

  (reg-sub
    :db
    (fn [db _]
      db))

  (reg-sub
    :border-pos
    (fn [db _]
      (:border-pos db)))

  (reg-sub
    :timer-running
    (fn [db _]
      (:timer-running db)))

  (reg-sub
    :paused
    (fn [db _]
      (:paused db)))

  (reg-sub
    :score
    (fn [db _]
      (:score db)))

  (reg-sub
    :flappy-y
    (fn [db _]
      (:flappy-y db)))

  (reg-sub
    :flappy-angle
    (fn [db]
      (let [{:keys [time-delta]
             {:keys [gravity initial-vel]} :options} db]
        (floor ( * -1 (- initial-vel (* time-delta gravity)))))))

  (reg-sub
    :jump-count
    (fn [db _]
      (:jump-count db)))

  (reg-sub
    :pillar-list
    (fn [db _]
      (:pillar-list db))))
