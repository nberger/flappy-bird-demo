(ns flappy-bird-demo.devcards.pillar
  (:require-macros
    [devcards.core :refer [defcard defcard-rg]])
  (:require
    [devcards.core :as devcards]
    [reagent.core :as reagent]
    [flappy-bird-demo.views :as views]))

(defcard-rg pillar
  [:div {:style {:position :relative
                 :height "400px"}}
   [views/pillar {:cur-x 300
                  :upper-height 120
                  :lower-height 150}]])

(defcard-rg pillar2
  [:div {:style {:position :relative
                 :height "500px"}}
   [views/pillar {:cur-x 250
                  :upper-height 150
                  :lower-height 180}]])
