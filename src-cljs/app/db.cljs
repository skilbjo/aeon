(ns app.db
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(def default-db
  {:text "Hello world!"})

(def local-storage-key "compojure")

(defn stuff->local-storage [stuff]
  (.setItem js/localStorage local-storage-key (str stuff)))
