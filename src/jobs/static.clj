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
               (let [dw-f '(jdbc/with-db-connection [cxn (env :ro-jdbc-db-uri)]
                             (->> "sql/dashboard.sql"
                                  io/resource
                                  slurp
                                  (jdbc/query cxn)
                                  (map #(update % :date coerce/to-sql-date))))
                     athena-f '(->> "athena/dashboard.sql"
                                  io/resource
                                  slurp
                                  sql/query-athena
                                  (map #(update % :date coerce/to-sql-date)))]
                 (if (env :jdbc-athena-uri)
                   (dw-f)
                   (athena-f))))
        data' (memoize data)]
    (util/render-markdown "static/dashboard"
                          {:body (data')})))
