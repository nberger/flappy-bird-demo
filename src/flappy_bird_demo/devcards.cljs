(ns flappy-bird-demo.devcards
  (:require-macros
    [devcards.core :refer [defcard defcard-rg]])
  (:require
    [devcards.core :as devcards]
    [reagent.core :as reagent]
    [flappy-bird-demo.views :as views]))

(defn flappy-card-component [data]
  (let [{:keys [y angle]} @data]
    [:div {:style {:position :relative
                   :height "120px"}}
     [views/flappy-pure y angle]]))

(defcard-rg flappy-default
  "Shows flappy with default angle"
  (fn [data _]
    [flappy-card-component data])
  {:y 0
   :angle 0}
  {:inspect-data true})

(defcard-rg flappy-looking-up
  "Shows flappy looking up (30 deg) to the sky"
  (fn [data _]
    [flappy-card-component data])
  {:y 0
   :angle -30}
  {:inspect-data true})

(defcard-rg flappy-looking-up-2
  "Shows flappy looking up (45 deg) to the sky"
  (fn [data _]
    [flappy-card-component data])
  {:y 0
   :angle -45}
  {:inspect-data true})

(defcard-rg flappy-looking-up-3
  "Shows flappy looking up (120 deg but should be capped at 75) to the sky"
  (fn [data _]
    [flappy-card-component data])
  {:y 0
   :angle -120}
  {:inspect-data true})

(defn start-devcards []
  (devcards/start-devcard-ui!))
