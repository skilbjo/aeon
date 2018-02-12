(ns jobs.api-test
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]
            [fixtures.api :as f]
            [fixtures.fixtures :refer [*cxn*] :as fix]
            [jobs.api :refer :all]))

(use-fixtures :each (fix/with-database))

(deftest unit-tests
  (testing "jobs.api unit tests"
    (is (= {:error {:msg "Error: '/api/made_up_dataset' is not a valid endpoint.\n                           Try /api/equities or /api/currency."}}
           (latest "made_up_dataset")))))

(deftest integration-test
  (->> "test/insert-source-data.sql"
       io/resource
       slurp
       (jdbc/execute! *cxn*))
  (testing "jobs.api integration test"
    (is (= (assoc {}
                  :body
                  (->> f/result
                       :body
                       (map #(update % :date coerce/to-sql-date))))
           (latest "currency")))))
