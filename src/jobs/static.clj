(ns jobs.static
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]
            [server.util :as util]))

(defn index []
  (util/render-markdown "index"
                        {:name "clojure developer"}))

(defn routes []
  (util/render-markdown "routes"))

(defn dashboard []
  (let [data (fn []
               (jdbc/with-db-connection [cxn (env :ro-jdbc-db-uri)]
                 (->> "sql/dashboard.sql"
                      io/resource
                      slurp
                      (jdbc/query cxn)
                      (map #(update % :date coerce/to-sql-date)))))
        data' (memoize data)]
    (util/render-markdown "static/dashboard"
                          {:body (data')})))
