(ns flappy-bird-demo.core
  (:require
   [reagent.core :as reagent]
   [re-frisk.core :refer [enable-re-frisk!]]
   [re-frame.registrar :as registrar]
   [re-frame.core :refer [dispatch subscribe dispatch-sync]]
   [cljs.core.async :refer [<! chan sliding-buffer put! close! timeout]]
   [flappy-bird-demo.devcards]
   [flappy-bird-demo.handlers :as handlers]
   [flappy-bird-demo.subs :as subs]
   [flappy-bird-demo.views :as views]))

(enable-console-print!)

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

    (handlers/register-handlers!)
    (subs/register-subs!)

    (enable-re-frisk! {:kind->id->handler? true})

    (when (empty? @(subscribe [:db]))
      (let [body (.-body js/document)]
        (.addEventListener body "keydown" #(handle-key-down %))))

    (dispatch-sync [:initialize-db])
    (reagent/render-component [views/main] node)
    (start-ticking!)))
