(ns ^:figwheel-hooks app.dev
  (:require [cljs-test-display.core :as cljs-display]
            [app.core-test]))

(defn ^:after-load run []
  (cljs.test/run-tests (cljs-display/init! :cljs-tests)
                       'app.core-test))

(defonce runit (run))
