(ns jobs.api-test
  (:require [buddy.core.codecs :as codecs]
            [buddy.core.hash :as hash]
            [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.test :refer :all]
            [fixtures.api :as f]
            [fixtures.fixtures :refer [*cxn*] :as fix]
            [jobs.api :as api]
            [server.sql :as sql]))

(use-fixtures :each (fix/with-database))

(deftest v1.unit-tests
  (testing "jobs.api unit tests"
    (is (= {:status 400
            :body "Error: '/api/made_up_dataset' is not a valid endpoint.\nTry /api/equities or /api/currency."}
           (api/v1.latest "made_up_dataset")))))

(deftest v1.integration-test
  (->> "test/insert-source-data.sql"
       io/resource
       slurp
       (jdbc/execute! *cxn*))
  (testing "jobs.api.latest integration test"
    (let [expected (assoc {}
                          :body
                          (->> f/result
                               :body
                               (map #(update % :date coerce/to-sql-date))))
          actual  (assoc {}
                         :body
                         (->> (api/v1.latest "currency")
                              :body
                              (map #(dissoc % :dw_created_at))))]
      (is (= expected
             actual))))

  (testing "jobs.api.portfolio integration test"
    (let [expected (assoc {}
                          :body
                          (->> f/result
                               :body
                               (map #(update % :date coerce/to-sql-date))))
          actual  (assoc {}
                         :body
                         (->> (-> {:user     "skilbjo"
                                   :password (-> "god"
                                                 sql/escape'
                                                 hash/sha256
                                                 codecs/bytes->hex)}
                                  api/v1.portfolio)
                              :body
                              (map #(dissoc % :dw_created_at))))]
      (is (= expected
             actual)))))
