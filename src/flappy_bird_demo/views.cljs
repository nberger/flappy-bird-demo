(ns flappy-bird-demo.views
  (:require
    [re-frame.core :refer [subscribe dispatch]]))

#_(defn jump [{:keys [cur-time jump-count] :as state}]
  (-> state
      (assoc
          :jump-count (inc jump-count)
          :flappy-start-time cur-time
          :initial-vel jump-vel)))

(comment
  (jump {:cur-time 1000
         :jump-count 3})
  )

(defn px [n] (str n "px"))

(comment
  (px 20)
  )

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
        [pillar p])]
     [:div.flappy {:style {:top (px flappy-y)}}]
     [:div.scrolling-border {:style {:background-position-x (px border-pos)}}]]))
