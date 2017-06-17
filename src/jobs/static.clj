(ns jobs.static
  (:require [server.sql :as sql]
            [server.util :as util]))

(defn index []
  (util/render-markdown "index"
                        {:name "clojure developer"}))

(defn routes []
  (util/render-markdown "routes"))

(defn index-mustache []
  (util/render-template "index"
                        {:name "clojure developer"}))
