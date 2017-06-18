(ns jobs.static
  (:require [clojure.java.io :as io]
            [server.sql :as sql]
            [server.util :as util]))

(defn index []
  (util/render-markdown "index"
                        {:name "clojure developer"}))

(defn routes []
  (util/render-markdown "routes"))

(defn dashboard-helper []
  (let [sql           (-> "sql/dashboard.sql"
                          (io/resource)
                          (slurp))
        rs            (memoize (fn []
                                 (sql/query sql)))
        transformed (->> (rs)
                         (util/map-seq-fkv-v util/date-me))]
    {:body transformed}))

(defn dashboard []
  (let [data    (dashboard-helper)
        _ (println data)]
    (util/render-markdown "static/dashboard" data)))

