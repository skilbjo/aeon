(ns jobs.api
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [server.sql :as sql]
            [server.util :as util]))

(def ^:private datasets
  #{:currency
    :economics
    :interest_rates
    :real_estate
    :equities})

(defn- allowed-endpoint? [coll needle]
  (->> needle
       keyword
       (contains? coll)))

(defn latest [dataset]
  (if (false? (allowed-endpoint? datasets dataset))
    {:error {:msg (format "Error: '/api/%s' is not a valid endpoint.
                           Try /api/equities or /api/currency." dataset)}}
    (let [data  (fn []
                  (let [dir (if (env :jdbc-athena-uri)
                              "athena"
                              "dw")
                        f   (if (env :jdbc-athena-uri)
                              sql/query-athena
                              sql/query')]
                    (->> (-> (str dir "/latest.sql")
                             io/resource
                             slurp
                             (f {:table dataset}))
                         (map #(update % :date coerce/to-sql-date)))))
          data' (memoize data)]
      {:body (data')})))
