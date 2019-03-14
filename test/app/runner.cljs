(ns ^:figwheel-hooks app.runner
  (:require [cljs.test :refer-macros [run-tests]]
            [cljs-test-display.core :as cljs-display]
            [app.core-test])
  (:require-macros [cljs.test]))

(enable-console-print!)

(defn run-all-tests []
  (run-tests 'app.core-test))

(defn ^:after-load run []
  (cljs.test/run-tests (cljs-display/init! :cljs-tests)
                       'app.core-test))

(defonce runit (run))
