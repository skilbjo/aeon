(ns app.subs
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :refer [reg-sub subscribe]]))

(defn sorted-stuffs [db _]
  (println "in subs:" db)
  (:stuffs db))

(reg-sub
  :sorted-stuffs
  sorted-stuffs)

(reg-sub
  :stuffs
  (fn-traced [query-v _]
    (subscribe [:sorted-stuffs])))
