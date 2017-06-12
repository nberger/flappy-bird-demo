(ns flappy-bird-demo.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(defn register-subs! []

  (reg-sub
    :db
    (fn [db _]
      db))

  (reg-sub
    :pillar-list
    (fn [db _]
      (:pillar-list db))))
