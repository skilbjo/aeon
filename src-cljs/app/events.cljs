(ns app.events
  (:require [app.db :refer [default-db stuff->local-storage]]
            [cljs.spec.alpha :as s]
            [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after]]))

#_(reg-event-fx
  :initialize-db
  [(inject-cofx :local-store-todos) check-spec-interceptor]
  (fn [{:keys [db local-store-todos]} _]
    {:db (assoc default-db :todos local-store-todos)}))
