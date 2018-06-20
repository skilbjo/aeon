(ns app.db
  (:require [cljs.reader :as reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

;; spec
(s/def ::id int?)
(s/def ::text string?)
(s/def ::stuff (s/keys :req-un [::id ::text]))
(s/def ::stuffs (s/and
                 (s/map-of ::id ::stuff)
                 #(instance? PersistentTreeMap %)))
(s/def ::db (s/keys :req-un [::stuffs]))

;; main
(def default-db
  {:stuffs (sorted-map)})

(def local-storage-key "compojure")

(defn stuffs->local-storage [stuffs]
  (.setItem js/localStorage local-storage-key (str stuffs)))

(re-frame/reg-cofx
  :local-store-stuffs
  (fn [cofx _]
      (assoc cofx :local-store-stuffs
             (into (sorted-map)
                   (some->> (.getItem js/localStorage "compojure")
                            (reader/read-string))))))
