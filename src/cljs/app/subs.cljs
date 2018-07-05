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

(reg-sub
 :active-page           ;; usage: (subscribe [:showing])
 (fn-traced [db _]             ;; db is the (map) value stored in the app-db atom
            (:active-page db)))  ;; extract a value from the application state

(reg-sub
 :user  ;; usage: (subscribe [:user])
 (fn-traced [db _]
            (:user db)))
