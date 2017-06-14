(ns flappy-bird-demo.devcards.flappy
  (:require-macros
    [devcards.core :refer [defcard defcard-rg deftest]])
  (:require
    [devcards.core :as devcards]
    [cljs.test :refer [testing is]]
    [reagent.core :as reagent]
    [cljsjs.rc-slider]
    [flappy-bird-demo.views :as views]))

(defn flappy-card-component [data]
  (let [{:keys [y angle]} @data]
    [:div {:style {:position :relative
                   :margin-top "30px"
                   :height "120px"}}
     [views/flappy-pure y angle]]))

(def rc-slider
  (reagent/adapt-react-class js/Slider))

(defcard-rg flappy-default
  "Shows flappy with default angle. Adjust angle using the slider to see how flappy rotates"
  (fn [data _]
    [:div
     [:div {:style {:width "50%"}}
      [rc-slider {:default-value (:angle @data)
                  :on-change #(swap! data assoc :angle %)
                  :min -180 :max 180}]]
     [flappy-card-component data]])
  (reagent/atom {:y 0
                 :angle 0})
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

(deftest smooth-angle-test
  (testing "angle keeps at 0 until it gets to 30 or -30"
    (is (= 30
           (views/smooth-angle 30)))
    (is (= -30
           (views/smooth-angle -30)))
    (is (= 0
           (views/smooth-angle 0)
           (views/smooth-angle 10)
           (views/smooth-angle 18)
           (views/smooth-angle 29)
           (views/smooth-angle -15)
           (views/smooth-angle -29))))

  (testing "angle doesn't go higher than 75 degrees"
    (is (= 75
           (views/smooth-angle 75)
           (views/smooth-angle 90)
           (views/smooth-angle 182))))
  (testing "angle doesn't go lower than -75 degrees"
    (is (= -75
           (views/smooth-angle -75)
           (views/smooth-angle -90)
           (views/smooth-angle -182)))))

(defn start-devcards []
  (devcards/start-devcard-ui!))
