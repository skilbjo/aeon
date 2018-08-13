(ns fixtures.fixtures
  (:require [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.java.shell :as shell]
            [environ.core :refer [env]]))

(def ^:dynamic *cxn*
  nil)

(defn with-database []
  (fn [f]
    (binding [*cxn* (env :test-jdbc-db-uri)]
      (shell/with-sh-dir "dev-resources"
        (shell/sh "bash" "-c" "test/cp-ddl"))

      ;; aeon
      (->> "test/aeon-ddl.sql"
           io/resource
           slurp
           (jdbc/execute! *cxn*))

      ;; markets-etl
      (->> "test/ddl.sql"
           io/resource
           slurp
           (jdbc/execute! *cxn*))
      (f)

      ;; aeon
      (->> "drop schema aeon cascade;"
           (jdbc/execute! *cxn*))

      ;; markets-etl
      (->> "drop schema dw cascade;"
           (jdbc/execute! *cxn*))
      (shell/with-sh-dir "dev-resources"
        (shell/sh "bash" "-c" "rm test/ddl.sql")))))
