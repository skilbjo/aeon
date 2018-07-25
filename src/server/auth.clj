(ns server.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [environ.core :refer [env]]
            [server.sql :as sql]
            [server.util :as util]))

#_(def tokens #{:2f904e245c1f5 :skilbjo
                :45c1f5e3f05d0 :foouser
                :1})

;; TODO passwords are hashed, but is there a better way?
(def tokens
  (when-not *compile-files* ;; evaluate at run-time, not at compile-time
    (let [f   (if (env :jdbc-athena-uri)
                sql/query-athena
                sql/query')]
      (->> (util/multi-line-string "select password "
                                   "from aeon.users "
                                   "group by 1")
           f))))

(defn authfn [request token]
  #_(if (= *auth-key-in* token) ;; TODO implement jwt
      :auth-key
      nil)
  (let [token (-> token keyword)]
    (get tokens token)))

(def backend
  (backends/token {:authfn authfn}))

(defn token-auth
  [handler]
  (wrap-authentication handler backend))
