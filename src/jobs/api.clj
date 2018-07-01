(ns jobs.api
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [server.spec :as s]
            [server.sql :as sql]
            [server.util :as util]))

(defn v1.login [{:keys [user password]}]
  (let [user-query (util/multi-line-string "select                  "
                                           " username,              "
                                           " password               "
                                           "from aoin.users         "
                                           "where                   "
                                           " username = ':user' and "
                                           " password = ':password' "
                                           "limit 1                 ")
        result (-> user-query
                   (sql/query' {:user user
                                :password password})
                   first)
        unauthorized {:status 401
                      :body "Wrong username, password, or both, bucko"}]
    (if (and (= (:username result) user)
             (= (:password result) password))
      {:token "2f904e245c1f5"}
      unauthorized)))

(defn v1.latest [dataset]
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

(defn v1.quote [dataset ticker date]
  (log/info "params are: " dataset ticker date)
  (if (not (s/allowed-endpoint? s/datasets dataset))
    {:status 400
     :body (util/multi-line-string
            "Error: problem with dataset, ticker, or date."
            "Try '/api/equities/FB/2018-04-01'.")}
    (let [data  (fn [_]
                  (let [dir (if (env :jdbc-athena-uri)
                              "athena"
                              "dw")
                        f   (if (env :jdbc-athena-uri)
                              sql/query-athena
                              sql/query')]
                    (->> (-> (str dir "/quote.sql")
                             io/resource
                             slurp
                             (f {:table  dataset
                                 :ticker ticker
                                 :date   date}))
                         (map #(update % :date coerce/to-sql-date)))))
          data' (memoize data)]
      {:body (data' util/now')})))
