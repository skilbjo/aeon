(ns jobs.api
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [server.sql :as sql]
            [server.util :as util]))

(def ^:private datasets
  #{:currency
    :economics
    :interest_rates
    :real_estate
    :equities})

(defn latest [dataset]
  (if (false? (util/allowed-endpoint? datasets dataset))
    {:error {:msg (format "Error: '/api/%s' is not a valid endpoint.
                           Try /api/equities or /api/currency." dataset)}}
    (let [sql           (-> "sql/latest.sql"
                            (io/resource)
                            (slurp))
          rs            (memoize (fn []
                                   (sql/query sql
                                              {:table dataset})))
          transformed (->> (rs)
                           (util/map-seq-fkv-v util/date-me))]
      {:body transformed})))

(defn dashboard-helper []
  (let [sql           (-> "sql/dashboard.sql"
                          (io/resource)
                          (slurp))
        rs            (memoize (fn []
                                 (sql/query sql)))
        transformed (->> (rs)
                         (util/map-seq-fkv-v util/date-me)
                           ;first
)]
    {:body transformed}))

(defn dashboard []
  (let [data    (dashboard-helper)
        _ (println data)]
    (util/render-markdown "dashboard" data)))
