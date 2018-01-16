(ns server.sql
  (:require [clojure.string :as string]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]])
  (:import [java.sql DriverManager Connection]
           [java.util Properties]))

(clojure.lang.RT/loadClassForName "org.postgresql.Driver")

(defn- prepare-statement
  [sql params]
  (loop [sql sql
         kvs (map identity params)]
    (if (empty? kvs)
      sql
      (let [[[k v] & others] kvs]
        (recur (string/replace sql (str k) (str (jdbc/sql-value v)))
               others)))))

; https://stackoverflow.com/questions/11312155/how-to-use-a-tablename-variable-for-a-java-prepared-statement-insert
(defn query'
  ([sql]
   (query' sql {}))
  ([sql params]
   (with-open [cxn (-> :ro-jdbc-db-uri env DriverManager/getConnection)]
     (let [sql     (-> sql
                       (string/replace #";" "")
                       (string/replace #"--" "")
                       (string/replace #"\/" "")
                       (string/replace #"\/\*" "")
                       (string/replace #"\*\\" "")
                       (prepare-statement params))
           results (-> cxn
                       (.createStatement)
                       (.executeQuery sql))]
       (jdbc/metadata-result results)))))

(defn query-athena
  ([sql]
   (query-athena sql {}))
  ([sql params]
   (with-open [conn (DriverManager/getConnection (env :jdbc-athena-uri))]
     (let [sql     (-> sql
                       (string/replace #";" "")
                       (string/replace #"--" "")
                       #_(string/replace #"\/" "") ; removes America/Los_Angeles
                       (string/replace #"\/\*" "")
                       (string/replace #"\*\\" "")
                       (prepare-statement params))
           results (-> conn
                       (.createStatement)
                       (.executeQuery sql))]
       (jdbc/metadata-result results)))))
