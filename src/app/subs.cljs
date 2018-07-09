(ns app.subs
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]))

(rf/reg-sub
 :active-page
 (fn-traced [db _]
            (:active-page db)))

(rf/reg-sub
 :user
 (fn-traced [db _]
            (:user db)))

(rf/reg-sub
 :errors
 (fn [db _]
   (:errors db)))

(rf/reg-sub
 :loading
 (fn [db _]
   (:loading db)))
