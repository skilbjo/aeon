(ns server.routes
  (:require [buddy.core.hash :as hash]
            [buddy.core.codecs :as codecs]
            [clojure.string :as string]
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
            [server.auth :as auth]
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

    (api/context "" []
      (api/POST "/login" []
        :summary "Login, for an authentication token"
        :header-params [{x-csrf-token :- :server.spec/authorization nil}]
        :body-params [user     :- :server.spec/user
                      password :- :server.spec/password]
        (let [user-trusted     (-> user sql/escape util/lower-trim)
              password-trusted (-> password
                                   sql/escape'
                                   util/lower-trim
                                   hash/sha256
                                   codecs/bytes->hex)]
          (-> {:user user-trusted
               :password password-trusted}
              jobs.api/v1.login))))

    (api/context "/prices/:dataset" [dataset]
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
              response))))

    (api/context "/reports" []
      (api/GET "/portfolio" []
        :summary "How's the portfolio doing?"
        :header-params [authorization :- :server.spec/authorization]
        :middleware [auth/token-auth middleware/authenticated]
        (-> {:msg "You made it!"}
            response)))))

(def swagger
  (-> {:swagger
       {:ui   "/swagger"
        :spec "/swagger.json"
        :middleware [ring-json/wrap-json-response
                     anti-forgery/wrap-anti-forgery]
        :data {:info {:title       "Aoin API"
                      :description "A webserver in LISP FTW"
                      :version     "1.0.0"}}}}
      (api/api api-routes)))

(defroutes combined-routes
  (-> server-routes
      (ring-defaults/wrap-defaults (assoc
                                    ring-defaults/site-defaults
                                    :security
                                    {:anti-forgery false
                                     :hsts true
                                     :content-type-options :nosniff
                                     :frame-options :sameorigin
                                     :xss-protection {:enable? true
                                                      :mode :block}}))
      #_anti-forgery/wrap-anti-forgery)

  (-> clojurescript-routes
      (ring-defaults/wrap-defaults (assoc
                                    ring-defaults/site-defaults
                                    :security
                                    {:anti-forgery false
                                     :hsts true
                                     :content-type-options :nosniff
                                     :frame-options :sameorigin
                                     :xss-protection {:enable? true
                                                      :mode    :block}}))
      #_anti-forgery/wrap-anti-forgery)

  swagger

  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> combined-routes
      #_(middleware/add-content-security-policy
         :config-path
         "policy/content_security_policy.clj")
      #_(middleware/wrap-referrer-policy "strict-origin")
      #_(session/wrap-session {:cookie-attrs {:max-age 3600
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
