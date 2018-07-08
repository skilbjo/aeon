(ns app.subs
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]))

(defn sorted-stuffs [db _]
  (println "in subs:" db)
  (:stuffs db))

(rf/reg-sub
 :sorted-stuffs
 sorted-stuffs)

(rf/reg-sub
 :stuffs
 (fn-traced [query-v _]
            (rf/subscribe [:sorted-stuffs])))

(rf/reg-sub
 :active-page           ;; usage: (subscribe [:showing])
 (fn-traced [db _]             ;; db is the (map) value stored in the app-db atom
            (:active-page db)))  ;; extract a value from the application state

(rf/reg-sub
 :user  ;; usage: (subscribe [:user])
 (fn-traced [db _]
            (:user db)))

(rf/reg-sub
 :errors  ;; usage: (subscribe [:errors])
 (fn [db _]
   (:errors db)))
