(ns server.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [environ.core :refer [env]]
            [server.sql :as sql]
            [server.util :as util]))

;; TODO make tokens {:skilbjo "token-here"}, not #{:"token-here"}
(def tokens ;; TODO passwords are hashed, but is there a better way?
  (delay
   (let [query  (util/multi-line-string "select password "
                                        "from aeon.users "
                                        "group by 1      ")
         f      (if (env :jdbc-athena-uri)
                  sql/query-athena
                  sql/query')]
     (->> query
          f
          (map #(:password %))
          (map keyword)
          set))))

(defn authfn [request token]
  #_(if (= *auth-key-in* token) ;; TODO implement jwt
      :auth-key
      nil)
  (let [token (-> token keyword)]
    (get @tokens token)))

(def backend
  (backends/token {:authfn authfn}))

(defn token-auth
  [handler]
  (wrap-authentication handler backend))
