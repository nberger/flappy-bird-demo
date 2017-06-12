(ns flappy-bird-demo.core
  (:require
   [cljsjs.react]
   [cljsjs.react.dom]
   [reagent.core :as reagent]
   [cljs.core.async :refer [<! chan sliding-buffer put! close! timeout]]
   [flappy-bird-demo.views :as views])
  (:require-macros
   [cljs.core.async.macros :refer [go-loop go]]))

(enable-console-print!)

(defonce flap-state (atom views/starting-state))

(defn time-loop [time]
  (let [new-state (swap! flap-state (partial views/time-update time))]
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
  [:div.board { :onMouseDown (fn [e]
                               (swap! flap-state views/jump)
                               (.preventDefault e))}
   [:h1.score score]
   (if-not timer-running
     [:a.start-button {:onClick #(start-game)}
      (if (< 1 jump-count) "RESTART" "START")]
     [:span])
   [:div (map views/pillar pillar-list)]
   [:div.flappy {:style {:top (views/px flappy-y)}}]
   [:div.scrolling-border {:style { :background-position-x (views/px border-pos)}}]])

(defn renderer [node full-state]
  (reagent/render-component (main-template full-state) node))

(defn init []
  (let [node (.getElementById js/document "board-area")]
    (add-watch flap-state :renderer (fn [_ _ _ n]
                                      (renderer node (views/world n))))
    (reset! flap-state @flap-state)))
