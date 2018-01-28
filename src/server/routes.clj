(ns server.routes
  (:require [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [jobs.api :as jobs.api]
            [jobs.static :as jobs.static]
            [server.middleware-policy :as policy]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.json :as ring-json]
            [ring.middleware.session :as session]
            [ring.util.response :refer [response]]
            [server.util :as util])
  (:gen-class))

(defroutes site-routes
  (HEAD "/" [])
  (GET "/" []
    (jobs.static/index))
  (GET "/routes" []
    (jobs.static/routes))
  (GET "/dashboard" []
    (jobs.static/dashboard)))

(defroutes api-routes
  (GET "/api/:dataset/latest" [dataset]
    (let [dataset-trusted (-> dataset
                              (string/replace #"\;" "")
                              (string/replace #"\-" "")
                              (string/replace #"\/" "")
                              (string/replace #"\/\*" "")
                              (string/replace #"\*\\" ""))
          response'  (jobs.api/latest dataset-trusted)]
      (-> response'
          response))))

(defroutes combined-routes
  (ring-defaults/wrap-defaults site-routes
                               (assoc
                                ring-defaults/site-defaults
                                :security
                                {:anti-forgery true
                                 :hsts true
                                 :content-type-options :nosniff
                                 :frame-options :sameorigin
                                 :xss-protection {:enable? true :mode :block}}))

  (ring-json/wrap-json-response api-routes)

  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> combined-routes
      (policy/add-content-security-policy :config-path
                                          "policy/content_security_policy.clj")
      (policy/wrap-referrer-policy "strict-origin")
      anti-forgery/wrap-anti-forgery
      (session/wrap-session {:cookie-attrs {:max-age 3600
                                            :secure  true}})))

(defn -main []  ; java -jar app.jar uses this as the entrypoint
  ; schedule the healthchecks
  (util/schedule-healthchecks.io)

  ; start the server
  (jetty/run-jetty app
                   {:send-server-version? false
                    :port                 8080
                    :ssl-port             8443
                    :keystore             "/java_key_store"
                    :key-password         (env :quandl-api-key)
                    :ssl?                 true}))
