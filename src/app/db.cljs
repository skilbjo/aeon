(ns app.db
  (:require [cljs.reader :as reader]
            [re-frame.core :as rf]))

(def local-storage-key "aeon")

(defn set-user-ls [user]
  (.setItem js/localStorage local-storage-key (str user)))

(defn remove-user-ls [user]
  (.removeItem js/localStorage local-storage-key))

;; main
(def default-db
  {:active-page :home})

(rf/reg-cofx
 :local-store-user
 (fn [cofx _]
   (assoc cofx :local-store-user
          (into (sorted-map)
                (some->> (.getItem js/localStorage local-storage-key)
                         (reader/read-string)))))) ; EDN map -> map
