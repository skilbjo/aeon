(ns jobs.static-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [fixtures.fixtures :refer [*cxn*] :as fix]
            [fixtures.static :as f]
            [jobs.static :refer :all]))

(use-fixtures :each (fix/with-database))

(deftest integration-test
  (->> "test/insert-source-data.sql"
       io/resource
       slurp
       (jdbc/execute! *cxn*))
  (testing "jobs.static integration test"
    (is (= f/result
           (dashboard)))))
