(ns server.util-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as time]
            [clj-time.format :as formatter]
            [server.util :as util]))

(deftest v1.unit-tests
  (testing "server.util string tests"
    (is (= "this has been lower trimmed"
           (util/lower-trim " THIS HAS BEEN LOWER TRIMMED "))))

  (testing "server.util date tests (resolves issue #53)"
    (let [date  (time/date-time 2020 01 01)]
      (is (= 2020
             (-> date time/year)))
      (is (= 2019
             (->> date
                  (formatter/unparse util/pst-formatter)
                  formatter/parse
                  time/year)))
      (is (not= date
                (->> date
                     (formatter/unparse util/pst-formatter)
                     formatter/parse))))))
