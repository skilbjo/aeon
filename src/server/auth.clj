(ns server.auth
  (:require [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]))

(defn authfn [request token]
  (if (= *auth-key-in* token)
    :auth-key
    nil))

(def backend-header
  (backends/token {:authfn authfn}))

(defn token-auth
  [handler]
  (wrap-authentication handler backend-header))
