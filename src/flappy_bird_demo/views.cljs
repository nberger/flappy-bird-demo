(ns flappy-bird-demo.views
  (:require
   [cljsjs.react]
   [cljsjs.react.dom]))

(defn jump [{:keys [cur-time jump-count] :as state}]
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
