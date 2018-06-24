(ns jobs.api
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [server.spec :as s]
            [server.sql :as sql]
            [server.util :as util]))

(defn latest [dataset]
  (if (false? (s/allowed-endpoint? s/datasets dataset))
    {:status 400
     :body (util/multi-line-string (format
                                    "Error: '/api/%s' is not a valid endpoint."
                                    dataset)
                                   "Try /api/equities or /api/currency.")}
    (let [data  (fn [_]
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
      {:body (data' util/now')}))) ; cache the request by date
