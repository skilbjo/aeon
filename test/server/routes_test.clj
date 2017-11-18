(ns server.routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [server.routes :refer :all]))

(def user-agent
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")

(deftest test-app
  (testing "main route"
    (let [response (app (-> (mock/request :get "/")
                            (mock/header "user-agent" user-agent)
                            ))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (app (-> (mock/request :get "/invalid")
                            (mock/header "user-agent" user-agent)
                            ))]
      (is (= (:status response) 404)))))
