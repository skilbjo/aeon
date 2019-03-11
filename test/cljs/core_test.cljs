(ns cljs.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]))

;; TODO https://github.com/bhauman/cljs-test-display
;; https://clojurescript.org/tools/testing

(deftest hello-world
  (testing "Cljs test hello world"
    (is (= 1 1))))
