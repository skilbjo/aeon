(ns app.db
  (:require [cljs.reader :as reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(def local-storage-key "compojure")

(defn stuffs->local-storage [stuffs]
  (.setItem js/localStorage local-storage-key (str stuffs)))

(def ->local-store (re-frame/after stuffs->local-storage))

(defn allocate-next-id [stuffs]
  ((fnil inc 0) (last (keys stuffs))))

;; main
(def default-db
  {:stuffs (sorted-map)})

(re-frame/reg-cofx
  :local-store-stuffs
  (fn [cofx _]
      (assoc cofx :local-store-stuffs
             (into (sorted-map)
                   (some->> (.getItem js/localStorage "compojure")
                            (reader/read-string))))))
