(ns jobs.static
  (:require [server.sql :as sql]
            [server.util :as util]))

(defn index []
  (util/render-template "index"
                        {:name "clojure developer"}))

(defn index-md []
  (util/render-markdown "index"
                        {:name "clojure developer"}))
