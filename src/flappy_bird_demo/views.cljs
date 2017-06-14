(ns flappy-bird-demo.views
  (:require
    [re-frame.core :refer [subscribe dispatch]]))

(defn px [n] (str n "px"))

(defn pillar [{:keys [cur-x pos-x upper-height lower-height]}]
  [:div.pillars
   [:div.pillar.pillar-upper {:style {:left (px cur-x)
                                       :height upper-height}}]
   [:div.pillar.pillar-lower {:style {:left (px cur-x)
                                       :height lower-height}}]])

(defn main []
  (let [pillar-list @(subscribe [:pillar-list])
        {:keys [score cur-time jump-count
                timer-running border-pos
                flappy-y paused]} @(subscribe [:db])]
    [:div.board {:on-mouse-down (fn [e]
                                  (dispatch [:jump])
                                  (.preventDefault e))}
     [:h1.score score]
     (cond
       (not timer-running)
       [:button.start-button {:on-click #(dispatch [:start-game])}
        (if (< 1 jump-count) "RESTART" "START")]

       paused
       [:button.start-button {:on-click #(dispatch [:toggle-pause])}
        "PAUSED"])
     [:div
      (for [p pillar-list]
        ^{:key (:key p)}
        [pillar p])]
     [:div.flappy {:style {:top (px flappy-y)}}]
     [:div.scrolling-border {:style {:background-position-x (px border-pos)}}]]))
