(ns controller
  (:require [model :as model]
            [util :as util]))

; Main
(defn index []
  (util/render-template "index"
                        {:name "clojure developer"}))
