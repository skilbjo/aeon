(ns server.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(def tokens {:2f904e245c1f5 :skilbjo
             :45c1f5e3f05d0 :foouser})

(defn authfn [request token]
  ; TODO implement jwt
  #_(if (= *auth-key-in* token)
      :auth-key
      nil)
  (println "in authfn")

  (let [token (-> token keyword)]
    (println token)
    (get tokens token nil)))

(def backend
  (backends/token {:authfn authfn}))

(defn token-auth
  [handler]
  (wrap-authentication handler backend))
