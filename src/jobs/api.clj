(ns jobs.api
  (:require [clojure.string :as string]
            [server.sql :as sql]
            [server.util :as util]))

;(defn query-quandl
  ;[dataset ticker & paramz]
  ;{:pre [(every? true? (util/allowed? paramz))]}
  ;(let [params   (first paramz)

;(def ^:private allowed
  ;{:collapse     #{"none" "daily" "weekly" "monthly" "quarterly" "annual"}
   ;:transform    #{"none" "rdiff" "diff" "cumul" "normalize"}
   ;:order        #{"asc" "desc"}
   ;:rows         integer?
   ;:limit        integer?
   ;:column_index integer?
   ;:start_date   date-time?
   ;:end_date     date-time?})

;(defn allowed? [m]
  ;(->> m
       ;first
       ;(map (fn [[k v]]
              ;((allowed k) v)))))

(defn data []
  {:name "clojure developer"})

(defn data-latest [dataset]
  (let [sql           (util/multi-line-string "select *
                                               from dw.:table
                                               order by date desc
                                               limit 10")
        rs            (memoize (fn []
                                 (sql/query sql
                                            {:table dataset})))
        transformed (->> (rs)
                         (util/map-seq-fkv-v util/date-me))]
    {:body transformed}))
