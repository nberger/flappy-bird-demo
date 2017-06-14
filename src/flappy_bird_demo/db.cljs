(ns flappy-bird-demo.db
  (:require
    [cljs.spec.alpha :as spec]))

(spec/def ::horiz-vel float?)
(spec/def ::gravity float?)
(spec/def ::jump-vel float?)
(spec/def ::start-y integer?)
(spec/def ::bottom-y integer?)
(spec/def ::flappy-x integer?)
(spec/def ::flappy-width integer?)
(spec/def ::flappy-height integer?)
(spec/def ::pillar-spacing integer?)
(spec/def ::pillar-gap integer?)
(spec/def ::pillar-width integer?)

(spec/def ::options (spec/keys :req-un [::horiz-vel
                                     ::gravity
                                     ::jump-vel
                                     ::start-y
                                     ::bottom-y
                                     ::flappy-x
                                     ::flappy-width
                                     ::flappy-height
                                     ::pillar-spacing
                                     ::pillar-gap
                                     ::pillar-width]))

(spec/def ::timer-running boolean?)
(spec/def ::flappy-start-time integer?)
(spec/def ::start-time integer?)
(spec/def ::jump-count integer?)
(spec/def ::initial-vel float?)

;; pillars
(spec/def ::pos-x integer?)
(spec/def ::cur-x integer?)
(spec/def ::start-time integer?)
(spec/def ::gap-top integer?)
(spec/def ::upper-height integer?)
(spec/def ::lower-height integer?)

(spec/def ::pillar (spec/keys :req-un [::start-time ::pos-x ::cur-x ::gap-top]
                           :opt-un [::upper-height
                                    ::lower-height]))

(spec/def ::pillar-list (spec/* ::pillar))

(spec/def ::border-pos integer?)

(spec/def ::db (spec/keys :req-un [::options
                                   ::timer-running
                                   ::start-time
                                   ::initial-vel
                                   ::pillar-list]
                          :opt-un [::border-pos]))

(def ^:private start-y 312)

(def default-db
  {:options {:horiz-vel -0.15
             :gravity 0.05
             :jump-vel 21
             :start-y start-y
             :bottom-y 561
             :flappy-x 212
             :flappy-width 57
             :flappy-height 41
             :pillar-spacing 324
             :pillar-gap 50
             :pillar-width 86}
   :timer-running false
   :jump-count 0
   :initial-vel 0
   :start-time 0
   :flappy-start-time 0
   :flappy-y start-y
   :pillar-list
   [{:key 0
     :start-time 0
     :pos-x 900
     :cur-x 900
     :gap-top 200}]})
