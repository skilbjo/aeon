(ns jobs.static
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]
            [server.sql :as sql]
            [server.util :as util]))

(defn index []
  (util/render-markdown "index"
                        {:name "clojure developer"}))

(defn routes []
  (util/render-markdown "routes"))

(defn dashboard []
  (let [data (fn []
               (let [dir      (if (env :jdbc-athena-uri)
                                "athena"
                                "dw")
                     dw-f     (fn []
                                (jdbc/with-db-connection [cxn (env :ro-jdbc-db-uri)]
                                  (->> (str dir "/dashboard.sql")
                                       io/resource
                                       slurp
                                       (jdbc/query cxn)
                                       (map #(update % :date coerce/to-sql-date)))))
                     athena-f (fn []
                                (->> (str dir "/dashboard.sql")
                                     io/resource
                                     slurp
                                     sql/query-athena
                                     (map #(update % :date coerce/to-sql-date))))]
                 (if (env :jdbc-athena-uri)
                   (athena-f)
                   (dw-f))))
        data' (memoize data)]
    (util/render-markdown "static/dashboard"
                          {:body (data')})))
