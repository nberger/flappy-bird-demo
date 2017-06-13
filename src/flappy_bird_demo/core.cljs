(ns flappy-bird-demo.core
  (:require
   [cljsjs.react]
   [cljsjs.react.dom]
   [reagent.core :as reagent]
   [re-frisk.core :refer [enable-re-frisk!]]
   [re-frame.registrar :as registrar]
   [re-frame.core :refer [dispatch subscribe dispatch-sync]]
   [cljs.core.async :refer [<! chan sliding-buffer put! close! timeout]]
   [flappy-bird-demo.handlers :as handlers]
   [flappy-bird-demo.subs :as subs]
   [flappy-bird-demo.views :as views])
  (:require-macros
   [cljs.core.async.macros :refer [go-loop go]]))

(enable-console-print!)

(defn main-template []
  (let [pillar-list @(subscribe [:pillar-list])
        {:keys [score cur-time jump-count
                timer-running border-pos
                flappy-y]} @(subscribe [:db])]
    [:div.board {:on-mouse-down (fn [e]
                                  (dispatch [:jump])
                                  (.preventDefault e))}
     [:h1.score score]
     (if-not timer-running
       [:a.start-button {:on-click #(dispatch [:start-game])}
        (if (< 1 jump-count) "RESTART" "START")]
       [:span])
     [:div
      (for [p pillar-list]
        ^{:key (:cur-x p)}
        [views/pillar p])]
     [:div.flappy {:style {:top (views/px flappy-y)}}]
     [:div.scrolling-border {:style {:background-position-x (views/px border-pos)}}]]))

(defonce tick-interval
  (atom nil))

(defn start-ticking! []
  (when-not @tick-interval
    (reset! tick-interval (js/setInterval #(dispatch [:tick]) 30))))

(defn stop-ticking! []
  (when @tick-interval
    (js/clearInterval @tick-interval)
    (reset! tick-interval nil)))

(defn handle-key-down [e]
  (dispatch [:toggle-pause]))

(defn init []
  (let [node (.getElementById js/document "board-area")]
    (stop-ticking!)
    (registrar/clear-handlers :event)
    (registrar/clear-handlers :sub)

    (enable-re-frisk!)
    (handlers/register-handlers!)
    (subs/register-subs!)

    (when (empty? @(subscribe [:db]))
      (let [body (.-body js/document)]
        (.addEventListener body "keydown" #(handle-key-down %))))
    (dispatch-sync [:initialize-db])
    (reagent/render-component [main-template] node)
    (start-ticking!)))
