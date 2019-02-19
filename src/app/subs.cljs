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
 (fn-traced [db _]
            (:errors db)))

(rf/reg-sub
 :loading
 (fn-traced [db _]
            (:loading db)))

(rf/reg-sub
 :portfolio
 (fn-traced [db _]
            (:portfolio db)))

(rf/reg-sub
 :asset-type
 (fn-traced [db _]
            (:asset-type db)))
