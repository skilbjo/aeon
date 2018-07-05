(ns app.db
  (:require [cljs.reader :as reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(def local-storage-key "aion")

(defn stuffs->local-storage [stuffs]
  (.setItem js/localStorage local-storage-key (str stuffs)))

(def ->local-store (re-frame/after stuffs->local-storage))

(defn set-user-ls [user]
  (.setItem js/localStorage local-storage-key (str user)))

(defn remote-user-ls [user]
  (.removeItem js/localStorage local-storage-key))

;; main
(def default-db
  {:active-page :home})

(re-frame/reg-cofx
 :local-store-user
 (fn [cofx _]
   (assoc cofx :local-store-user
          (into (sorted-map)
                (some->> (.getItem js/localStorage local-storage-key)
                         (reader/read-string)))))) ; EDN map -> map
