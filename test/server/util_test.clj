(ns server.util-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [server.util :as util]))

(deftest v1.unit-tests
  (testing "server.util unit tests"
    (is (= "this has been lower trimmed"
           (util/lower-trim " THIS HAS BEEN LOWER TRIMMED ")))))
