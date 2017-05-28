(ns jobs.static
  (:require [server.sql :as sql]
            [server.util :as util]))

(defn index []
  (util/render-template "index"
                        {:name "clojure developer"}))

