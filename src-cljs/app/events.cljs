(ns app.events
  (:require [app.db :refer [default-db stuffs->local-storage]]
            [cljs.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :refer [reg-event-db
                                   reg-event-fx
                                   inject-cofx
                                   path
                                   after]]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db))
                    {}))))

(def check-spec-interceptor (after (partial check-and-throw :app.db/db)))

(def ->local-store (after stuffs->local-storage))

(def todo-interceptors [check-spec-interceptor
                        (path :stuffs)
                        ->local-store])

(defn allocate-next-id [stuffs]
  ((fnil inc 0) (last (keys stuffs))))

(reg-event-fx
  :initialize-db
  [(inject-cofx :local-store-stuffs) check-spec-interceptor]
  (fn-traced [{:keys [db local-store-stuffs]} _]
    {:db (assoc default-db :stuffs local-store-stuffs)}))
