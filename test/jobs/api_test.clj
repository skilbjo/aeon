(ns jobs.api-test
  (:require [clj-time.coerce :as coerce]
            [clojure.test :refer :all]
            [jobs.api :refer :all]
            [fixtures.api :as f]
            [fixtures.fixtures :refer [*cxn*] :as fix]))

(use-fixtures :each (fix/with-database))

(deftest integration-test
  (testing "jobs.api integration test"
    (is (= (assoc {}
                  :body
                  (->> f/result
                       :body
                       (map #(update % :date coerce/to-sql-date))))
           (latest "currency")))))
