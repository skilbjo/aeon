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
            [jobs.cljs :as jobs.cljs]
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
  (GET "/index" []
    (jobs.static/index))
  (GET "/routes" []
    (jobs.static/routes))
  (GET "/dashboard" []
    (jobs.static/dashboard)))

(defroutes cljs-routes
  (GET "/" []
    jobs.cljs/app)
  (GET "/cljs" []
    jobs.cljs/app))

(def api-routes
  (api/context "/api/v1" []
    :tags ["api"]
    :coercion :spec

    (api/context "" []
      :tags ["login"]
      ;; TODO does CSRF on /login make sense? How to make it work with swagger?
      ;; per https://github.com/edbond/CSRF - CSRF + ring + POST does not work
      ;; with compojure 1.2.0+ (we are on 1.6.1)
      #_:middleware    #_[anti-forgery/wrap-anti-forgery]
      #_:header-params #_[{x-csrf-token :- :server.spec/authorization nil}]
      (api/POST "/login" []
        :summary "Login, for an authentication token"
        :body-params [user     :- :server.spec/user
                      password :- :server.spec/password]
        (let [user-trusted     (-> user sql/escape util/lower-trim)
              password-trusted (-> password
                                   sql/escape'
                                   hash/sha256
                                   codecs/bytes->hex)]
          (-> {:user     user-trusted
               :password password-trusted}
              jobs.api/v1.login
              response))))

    (api/context "/prices/:dataset" [dataset]
      :tags ["prices"]
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
      :tags ["reports"]
      (api/GET "/portfolio" []
        :summary "How's the portfolio doing?"
        :header-params [authorization :- :server.spec/authorization]
        :middleware [auth/token-auth middleware/authenticated]
        :query-params [user     :- :server.spec/user
                       password :- :server.spec/password]
        (let [user-trusted     (-> user sql/escape util/lower-trim)
              password-trusted (-> password
                                   sql/escape')]
          (-> {:user     user-trusted
               :password password-trusted}
              jobs.api/v1.portfolio
              response)))

      (api/GET "/asset-type" []
               :summary "How's everything performing by asset type?"
               :header-params [authorization :- :server.spec/authorization]
               :middleware [auth/token-auth middleware/authenticated]
               :query-params [user     :- :server.spec/user
                              password :- :server.spec/password]
               (let [user-trusted     (-> user sql/escape util/lower-trim)
                     password-trusted (-> password
                                          sql/escape')]
                 (-> {:user     user-trusted
                      :password password-trusted}
                     jobs.api/v1.asset-type
                     response)))

      (api/GET "/capitalization" []
               :summary "How's everything performing by capitalization?"
               :header-params [authorization :- :server.spec/authorization]
               :middleware [auth/token-auth middleware/authenticated]
               :query-params [user     :- :server.spec/user
                              password :- :server.spec/password]
               (let [user-trusted     (-> user sql/escape util/lower-trim)
                     password-trusted (-> password
                                          sql/escape')]
                 (-> {:user     user-trusted
                      :password password-trusted}
                     jobs.api/v1.capitalization
                     response)))

      (api/GET "/investment-style" []
               :summary "How's everything performing by investment style?"
               :header-params [authorization :- :server.spec/authorization]
               :middleware [auth/token-auth middleware/authenticated]
               :query-params [user     :- :server.spec/user
                              password :- :server.spec/password]
               (let [user-trusted     (-> user sql/escape util/lower-trim)
                     password-trusted (-> password
                                          sql/escape')]
                 (-> {:user     user-trusted
                      :password password-trusted}
                     jobs.api/v1.investment-style
                     response)))

      (api/GET "/location" []
               :summary "How's everything performing by location?"
               :header-params [authorization :- :server.spec/authorization]
               :middleware [auth/token-auth middleware/authenticated]
               :query-params [user     :- :server.spec/user
                              password :- :server.spec/password]
               (let [user-trusted     (-> user sql/escape util/lower-trim)
                     password-trusted (-> password
                                          sql/escape')]
                 (-> {:user     user-trusted
                      :password password-trusted}
                     jobs.api/v1.location
                     response))))))

(def swagger
  (-> {:swagger
       {:ui   "/swagger"
        :spec "/swagger.json"
        :middleware [ring-json/wrap-json-response]
        :data {:info {:title       "Aeon API"
                      :description "A webserver in LISP FTW"
                      :version     "1.0.0"}}}}
      (api/api api-routes)))

(defroutes combined-routes
  (-> swagger
      (ring-defaults/wrap-defaults (assoc
                                    ring-defaults/api-defaults
                                    :security
                                    {:anti-forgery false ; for POST to work
                                     :hsts true
                                     :content-type-options :nosniff
                                     :frame-options        :sameorigin
                                     :xss-protection {:enable? true
                                                      :mode    :block}})))

  (-> server-routes
      (ring-defaults/wrap-defaults (assoc
                                    ring-defaults/site-defaults
                                    :security
                                    {:anti-forgery true
                                     :hsts true
                                     :content-type-options :nosniff
                                     :frame-options        :sameorigin
                                     :xss-protection {:enable? true
                                                      :mode    :block}}))
      anti-forgery/wrap-anti-forgery)

  (-> cljs-routes
      (ring-defaults/wrap-defaults (assoc
                                    ring-defaults/site-defaults
                                    :security
                                    {:anti-forgery true
                                     :hsts true
                                     :content-type-options :nosniff
                                     :frame-options        :sameorigin
                                     :xss-protection {:enable? true
                                                      :mode    :block}}))
      anti-forgery/wrap-anti-forgery)

  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> combined-routes
      (middleware/add-content-security-policy
       :config-path
       "policy/content_security_policy.clj")
      (middleware/wrap-referrer-policy "strict-origin")
      (session/wrap-session {:cookie-attrs {:max-age 3600
                                            :secure  true}})
      gzip/wrap-gzip))

(defn -main []  ; java -jar app.jar uses this as the entrypoint
  (log/info "Starting aeon webserver ... ")
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
