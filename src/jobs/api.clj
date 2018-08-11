(ns jobs.api
  (:require [clj-time.coerce :as coerce]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [server.spec :as s]
            [server.sql :as sql]
            [server.util :as util]))

(def login-error-counter
  (atom 0))

(defn v1.login [{:keys [user password]}]
  (let [dir     (if (env :jdbc-athena-uri)
                  "athena"
                  "dw")
        f       (if (env :jdbc-athena-uri)
                  sql/query-athena
                  sql/query'')
        result  (-> (->> "/login.sql"
                         (str dir)
                         io/resource
                         slurp)
                    (f {:user     user
                        :password password})
                    first)
        unauthorized (fn [user password]
                       (swap! login-error-counter inc)
                       (let [msg (str "Unauthorized Login Report\n\n"
                                      "Attempts: %s \n"
                                      "Attempted access with \n"
                                      "user: %s \n"
                                      "hashed-password: %s")
                             warn-msg (format msg
                                              @login-error-counter
                                              user
                                              password)]

                         (log/warn warn-msg)

                         ;; email on 10 unauth'd attempts & every 5 thereafter
                         (when (and (->> @login-error-counter
                                         (< 10))
                                    (-> @login-error-counter
                                        (mod 5)
                                        (= 0)))
                           (util/email "Unauthorized Login Report" warn-msg))

                         {:status 401
                          :body "Wrong username, password, or both, bucko"}))]
    (if (and (= (:user     result) user)
             (= (:password result) password))
      {:user      user      ;; TODO for token: password is
       :token     password} ;; hashed, but is there a better way?
      (unauthorized user password))))

(defn v1.portfolio [user]
  (let [data  (fn [_]
                (let [dir (if (env :jdbc-athena-uri)
                            "athena"
                            "dw")
                      f   (if (env :jdbc-athena-uri)
                            sql/query-athena
                            sql/query'')]
                  (->> (-> (str dir
                                "/portfolio"
                                (when (env :jdbc-athena-uri)
                                  "_athena")
                                ".sql")
                           io/resource
                           slurp
                           (f {:user user}))
                       (map #(update % :date coerce/to-sql-date)))))
        data' (memoize data)]
    {:body (data' util/now')}))

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
                             (f {:table (str dataset "_fact")}))
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
                             (f {:table  (str dataset "_fact")
                                 :ticker ticker
                                 :date   date}))
                         (map #(update % :date coerce/to-sql-date)))))
          data' (memoize data)]
      {:body (data' util/now')})))
