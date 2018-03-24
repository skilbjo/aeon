(ns server.routes
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [jobs.api :as jobs.api]
            [jobs.clojurescript :as jobs.clojurescript]
            [jobs.static :as jobs.static]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.gzip :as gzip]
            [ring.middleware.json :as ring-json]
            [ring.middleware.session :as session]
            [ring.util.response :refer [response]]
            [server.error :as error]
            [server.middleware-policy :as policy]
            [server.util :as util])
  (:gen-class))

(defroutes server-routes
  (HEAD "/" [])
  (GET "/" []
    (jobs.static/index))
  (GET "/routes" []
    (jobs.static/routes))
  (GET "/dashboard" []
    (jobs.static/dashboard)))

(defroutes clojurescript-routes
  (GET "/app" []
    (jobs.clojurescript/send-app)))

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
  (ring-defaults/wrap-defaults server-routes
                               (assoc
                                ring-defaults/site-defaults
                                :security
                                {:anti-forgery true
                                 :hsts true
                                 :content-type-options :nosniff
                                 :frame-options :sameorigin
                                 :xss-protection {:enable? true :mode :block}}))

  (ring-defaults/wrap-defaults  clojurescript-routes
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
                                            :secure  true}})
      gzip/wrap-gzip))

(defn -main []  ; java -jar app.jar uses this as the entrypoint
  (log/info "Starting compojure webserver ... ")
  (error/set-default-error-handler)

  ; schedule the healthchecks
  (util/schedule-healthchecks-io)

  ; start the server
  (jetty/run-jetty app
                   {:send-server-version? false
                    :port                 8080
                    :ssl-port             8443
                    :keystore             "/java_key_store"
                    :key-password         (env :quandl-api-key)
                    :ssl?                 true}))
