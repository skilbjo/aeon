(ns server.routes
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [compojure.api.sweet :as api]
            [compojure.core :refer [defroutes HEAD GET]]
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
            [server.middleware :as middleware]
            [server.sql :as sql]
            [server.util :as util]
            [spec-tools.spec :as spec])
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

(def api-routes
  (api/context "/api/v1" []
    :tags ["api"]
    :coercion :spec

    (api/context "/:dataset" [dataset]
      (api/GET "/latest" []
        :summary "Latest prices"
        (let [dataset-trusted (-> dataset sql/escape util/lower-trim)
              response'       (jobs.api/v1.latest dataset-trusted)]
          (-> response'
              response)))

      (api/GET "/" []
        :summary "Price for a specific date"
        :query-params [ticker :- :server.spec/ticker
                       date   :- :server.spec/date]
        (let [dataset-trusted (-> dataset sql/escape util/lower-trim)
              ticker-trusted  (-> ticker sql/escape util/lower-trim)
              date-trusted    (-> date sql/escape' util/lower-trim)
              response'       (jobs.api/v1.quote dataset-trusted
                                                 ticker-trusted
                                                 date-trusted)]
          (-> response'
              response))))))

(def api-routes
  (-> {:swagger
       {:ui   "/swagger"
        :spec "/swagger.json"
        :middleware [[ring-json/wrap-json-response]]
        :data {:info {:title       "Aoin API"
                      :description "A webserver in LISP FTW"
                      :version     "1.0.0"}}}}
      (api/api api-routes)))

(defroutes combined-routes
  (ring-defaults/wrap-defaults server-routes
                               (assoc
                                ring-defaults/site-defaults
                                :security
                                {:anti-forgery true
                                 :hsts true
                                 :content-type-options :nosniff
                                 :frame-options :sameorigin
                                 :xss-protection {:enable? true
                                                  :mode :block}}))

  (ring-defaults/wrap-defaults  clojurescript-routes
                                (assoc
                                 ring-defaults/site-defaults
                                 :security
                                 {:anti-forgery true
                                  :hsts true
                                  :content-type-options :nosniff
                                  :frame-options :sameorigin
                                  :xss-protection {:enable? true
                                                   :mode    :block}}))

  api-routes

  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> combined-routes
      (middleware/add-content-security-policy
       :config-path
       "policy/content_security_policy.clj")
      (middleware/wrap-referrer-policy "strict-origin")
      anti-forgery/wrap-anti-forgery
      (session/wrap-session {:cookie-attrs {:max-age 3600
                                            :secure  true}})
      gzip/wrap-gzip))

(defn -main []  ; java -jar app.jar uses this as the entrypoint
  (log/info "Starting aoin webserver ... ")
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
