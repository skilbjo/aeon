(ns app.events
  (:require [app.db :refer [default-db stuffs->local-storage]]
            [cljs.spec.alpha :as s]
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

(defn allocate-next-id
  "Returns the next todo id.
  Assumes todos are sorted.
  Returns one more than the current largest id."
  [stuffs]
  ((fnil inc 0) (last (keys stuffs))))

(reg-event-fx
  :initialize-db
  [(inject-cofx :local-store-stuffs) check-spec-interceptor]
  (fn [{:keys [db local-store-todos]} _]
    {:db (assoc default-db :todos local-store-todos)}))
