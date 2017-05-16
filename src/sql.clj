(ns sql
  (:refer-clojure :exclude [get])
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]
            [util :as util]
            [clj-time.jdbc])
  (:import [java.sql BatchUpdateException]
           [java.util Properties]
           [java.sql DriverManager Connection]))

(clojure.lang.RT/loadClassForName "org.postgresql.Driver")

(def internalize-identifier (comp string/lower-case util/dasherize))
(def internalize-map-identifier (comp keyword string/lower-case util/dasherize))

(defn get-dw-conn []
  (env :db-jdbc-uri))

(defn- prepare-statement
  [sql params]
  (loop [sql sql
         kvs (map identity params)]
    (if (empty? kvs)
      sql
      (let [[[k v] & others] kvs]
        (recur (string/replace sql (str k) (str (jdbc/sql-value v)))
               others)))))

(defn query
  ([sql]
   (query sql {}))
  ([sql params]
   (jdbc/with-db-connection [conn (get-dw-conn)]
     (let [sql     (prepare-statement sql params)
           _       (println sql)
           results (-> conn
                       (.createStatement)
                       (.executeQuery sql))]
       (jdbc/metadata-result results)))))

;(defn query [sql]
  ;(jdbc/with-db-connection [conn (get-dw-conn)]
    ;(jdbc/query conn sql)))

(defn insert-multi! [table data]
  (jdbc/with-db-connection [conn (get-dw-conn)]
    (jdbc/insert-multi! conn table data)))

(defn insert! [table data]
  (jdbc/with-db-connection [conn (get-dw-conn)]
    (jdbc/insert! conn table data)))

(defn update-or-insert! [table where-clause data]
  (jdbc/with-db-connection [conn (get-dw-conn)]
    (jdbc/with-db-transaction [conn conn]
      (let [result (jdbc/update! conn table data where-clause)]
        (if (zero? (first result))
          (insert! table data)
          result)))))

(defn all [params]
  (query "select * from :table"
         params))
