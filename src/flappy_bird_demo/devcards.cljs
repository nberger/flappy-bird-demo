(ns flappy-bird-demo.devcards
  (:require
    [devcards.core :as devcards]
    [flappy-bird-demo.devcards.pillar]
    [flappy-bird-demo.devcards.flappy]))

(defn start-devcards []
  (devcards/start-devcard-ui!))
