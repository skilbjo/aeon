(ns jobs.api-test
  (:require [clojure.test :refer :all]
            [jobs.api :refer :all]
            [fixtures.api :as f]
            [fixtures.fixtures :refer [*cxn*] :as fix]))

(use-fixtures :each (fix/with-database))

(deftest integration-test
  (testing "api integration test"
    (is (= f/result
           (latest "currency")))))
