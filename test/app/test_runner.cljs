(ns app.test-runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs-test-display.core]
            [app.core-test])
  (:require-macros [cljs.test]))

(enable-console-print!)

(defn run-all-tests []
  (run-tests 'app.core-test))

(defn run-tests-ui []
  ;; where "app" is the HTML node where you want to mount the tests
  (cljs.test/run-tests
    (cljs-test-display.core/init! "test-app") ;;<-- initialize cljs-test-display here
    'app.core-test))
