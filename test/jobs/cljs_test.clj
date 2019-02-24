(ns jobs.cljs-test
  (:require [clojure.test :refer :all]
            [fixtures.cljs :as f]
            [jobs.cljs :as cljs]))

(deftest integration-test
  (testing "jobs.cljs integration test"
    (is (= f/result
           cljs/app))))
