(ns app.events
  (:require [app.db :as db]
            [app.spec :as s]
            [cljs.spec.alpha :as spec]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as re-frame]
            #_[re-frame.core :refer [reg-event-fx
                                     inject-cofx
                                     path
                                     after]]))

#_(def todo-interceptors [s/check-spec-interceptor
                          (path :stuffs)
                          db/->local-store])

(re-frame/reg-event-fx
 :initialize-db
 [(re-frame/inject-cofx :local-store-stuffs) s/check-spec-interceptor]
 (fn-traced [{:keys [db local-store-stuffs]} _]
            {:db (-> db/default-db
                     (assoc :stuffs local-store-stuffs))}))
