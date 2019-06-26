(ns app.subs
  (:require [app.util :as util]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]))

(rf/reg-sub
 :active-page
 (fn-traced [db _]
            (:active-page db)))

(rf/reg-sub
 :user
 (fn-traced [db _]                               ; when false: [:user {:token nil, :user nil}]
            (when-let [user (-> db :user :user)] ; (:user db) is true even when failed login
              user)))                            ; thus, (-> db :user :user)

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
            (util/get-report db "portfolio")))

(rf/reg-sub
 :asset-type
 (fn-traced [db _]
            (util/get-report db "asset-type")))

(rf/reg-sub
 :capitalization
 (fn-traced [db _]
            (util/get-report db "capitalization")))

(rf/reg-sub
 :investment-style
 (fn-traced [db _]
            (util/get-report db "investment-style")))

(rf/reg-sub
 :location
 (fn-traced [db _]
            (util/get-report db "location")))
