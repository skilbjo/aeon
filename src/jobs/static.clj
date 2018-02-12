(ns jobs.static
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
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
                                (try
                                  (jdbc/with-db-connection [cxn (-> :ro-jdbc-db-uri env)]
                                    (->> (str dir "/dashboard.sql")
                                         io/resource
                                         slurp
                                         (jdbc/query cxn)
                                         (map #(update % :date coerce/to-sql-date))))
                                  (catch Exception ex
                                    (log/error ex "There was a problem fetching data from the db"))))
                     athena-f (fn []
                                (try
                                  (->> (str dir "/dashboard.sql")
                                       io/resource
                                       slurp
                                       sql/query-athena
                                       (map #(update % :date coerce/to-sql-date)))
                                  (catch Exception ex
                                    (log/error ex "There was a problem fetching data from the db"))))]
                 (if (env :jdbc-athena-uri)
                   (athena-f)
                   (dw-f))))
        data' (memoize data)]
    (util/render-markdown "static/dashboard"
                          {:body (data')})))
