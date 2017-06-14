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

(defn border []
  (let [border-pos @(subscribe [:border-pos])]
    [:div.scrolling-border {:style {:background-position-x (px border-pos)}}]))

(defn flappy-button []
  (let [jump-count @(subscribe [:jump-count])
        paused @(subscribe [:paused])
        timer-running @(subscribe [:timer-running])]
    (cond
      (not timer-running)
      [:button.start-button {:on-click #(dispatch [:start-game])}
       (if (< 1 jump-count) "RESTART" "START")]

      paused
      [:button.start-button {:on-click #(dispatch [:toggle-pause])}
       "PAUSED"])))

(defn flappy-pure [y angle]
  (let [angle (min 75 (max -75 angle))]
    [:div.flappy {:style {:top (px y)
                          :transform (str "rotate(" angle "deg)")}}]))

(defn flappy
  []
  (let [y @(subscribe [:flappy-y])
        angle @(subscribe [:flappy-angle])]
    [flappy-pure y angle]))

(defn main []
  (let [pillar-list @(subscribe [:pillar-list])
        score @(subscribe [:score])]
    [:div.board {:on-mouse-down (fn [e]
                                  (dispatch [:jump])
                                  (.preventDefault e))}
     [:h1.score score]
     [flappy-button]
     [:div
      (for [p pillar-list]
        ^{:key (:key p)}
        [pillar p])]
     [flappy]
     [border]]))
