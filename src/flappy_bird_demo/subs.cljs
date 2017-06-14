(ns flappy-bird-demo.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(defn register-subs! []

  (reg-sub
    :db
    (fn [db _]
      db))

  (reg-sub
    :border-pos
    (fn [db _]
      (:border-pos db)))

  (reg-sub
    :timer-running
    (fn [db _]
      (:timer-running db)))

  (reg-sub
    :paused
    (fn [db _]
      (:paused db)))

  (reg-sub
    :score
    (fn [db _]
      (:score db)))

  (reg-sub
    :flappy-y
    (fn [db _]
      (:flappy-y db)))

  (reg-sub
    :jump-count
    (fn [db _]
      (:jump-count db)))

  (reg-sub
    :pillar-list
    (fn [db _]
      (:pillar-list db))))
