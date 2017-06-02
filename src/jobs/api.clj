(ns jobs.api
  (:require [clojure.string :as string]
            [server.sql :as sql]
            [server.util :as util]))

(def ^:private api-endpoints
  #{:currency
    :economics
    :interest_rates
    :real_estate
    :equities})

(defn data []
  {:name "clojure developer"})

(defn data-latest [dataset]
  (if (false? (util/allowed-endpoint? api-endpoints dataset))
    {:error {:msg (format "Error: '/api/%s' is not a valid endpoint. Try /api/equities or /api/currency." dataset)}}
    (let [sql           (util/multi-line-string "select *
                                                 from dw.:table
                                                 order by date desc
                                                 limit 10")
          rs            (memoize (fn []
                                   (sql/query sql
                                              {:table dataset})))
          transformed (->> (rs)
                           (util/map-seq-fkv-v util/date-me))]
      {:body transformed})))
