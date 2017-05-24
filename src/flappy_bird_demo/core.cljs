(ns flappy-bird-demo.core
  (:require
   [cljsjs.react]
   [cljsjs.react.dom]
   [sablono.core :as sab :include-macros true]
   [cljs.core.async :refer [<! chan sliding-buffer put! close! timeout]]
   [flappy-bird-demo.views :as views])
  (:require-macros
   [cljs.core.async.macros :refer [go-loop go]]))

(enable-console-print!)

(defonce flap-state (atom views/starting-state))

(defn time-update [timestamp state]
  (-> state
      (assoc
          :cur-time timestamp
          :time-delta (- timestamp (:flappy-start-time state)))
      views/update-flappy
      views/update-pillars
      views/collision?
      views/score))

(defn time-loop [time]
  (let [new-state (swap! flap-state (partial time-update time))]
    (when (:timer-running new-state)
      (go
       (<! (timeout 30))
       (.requestAnimationFrame js/window time-loop)))))

(defn start-game []
  (.requestAnimationFrame
   js/window
   (fn [time]
     (reset! flap-state (views/reset-state @flap-state time))
     (time-loop time))))

(defn main-template [{:keys [score cur-time jump-count
                             timer-running border-pos
                             flappy-y pillar-list]}]
  (sab/html [:div.board { :onMouseDown (fn [e]
                                         (swap! flap-state views/jump)
                                         (.preventDefault e))}
             [:h1.score score]
             (if-not timer-running
               [:a.start-button {:onClick #(start-game)}
                (if (< 1 jump-count) "RESTART" "ART")]
               [:span])
             [:div (map views/pillar pillar-list)]
             [:div.flappy {:style {:top (views/px flappy-y)}}]
             [:div.scrolling-border {:style { :background-position-x (views/px border-pos)}}]]))

(let [node (.getElementById js/document "board-area")]
  (defn renderer [full-state]
    (.render js/ReactDOM (main-template full-state) node)))

(add-watch flap-state :renderer (fn [_ _ _ n]
                                  (renderer (views/world n))))

(reset! flap-state @flap-state)
