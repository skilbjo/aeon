(ns controller
  (:require [sql :as sql]
            [util :as util]))

; Main
(defn index []
  (util/render-template "index"
                        {:name "clojure developer"}))

; API
(defn data []
  {:name "clojure developer"})

(defn data-latest [dataset]
  (let [sql         (util/multi-line-string "select *
                                             from dw.:table
                                             order by date desc
                                             limit 10")
        rs          (memoize (fn []
                               (sql/query sql
                                          {:table dataset})))
        transformed (->> (rs)
                         (util/map-seq-fkv-v util/date-me))]
    {:body transformed}))
