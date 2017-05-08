(ns model
  (:refer-clojure :exclude [get])
  (:require [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]))

(defn get-dw-conn []
  (env :db-jdbc-uri))

(defn query [sql params]
  (with-open [conn (get-dw-conn)]
    (jdbc/query conn sql params)))

(def now
  (str (java.sql.Timestamp. (System/currentTimeMillis))))

(defn all []
  (query "select * from table"))
