(ns jobs.static-test
  (:require [clojure.test :refer :all]
            [jobs.static :refer :all]
            [fixtures.api :as f]
            [fixtures.fixtures :refer [*cxn*] :as fix]))

(use-fixtures :each (fix/with-database))

#_(deftest integration-test
    (testing "jobs.static integration test"
      (is (= 1
             (dashboard)
             ))))
