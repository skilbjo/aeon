(ns server.routes
  (:require [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [jobs.api :as jobs.api]
            [jobs.static :as jobs.static]
            [server.middleware-policy :as policy]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.json :as ring-json]
            [ring.middleware.session :as session]
            [ring.util.response :refer [response]])
  (:gen-class))

(defroutes site-routes
  (HEAD "/" []))
  (GET "/" []
    (jobs.static/index))
  (GET "/routes" []
    (jobs.static/routes))
  (GET "/dashboard" []
    (jobs.static/dashboard))

(defroutes api-routes
  (GET "/api/:dataset/latest" [dataset]
    (let [dataset-trusted  (string/escape dataset {\; "" \- ""})]
      (response (jobs.api/latest dataset-trusted)))))

(defroutes combined-routes
  (-> site-routes
      (ring-defaults/wrap-defaults (assoc ring-defaults/site-defaults
                                          :security {:content-type-options :nosniff
                                                     :anti-forgery   true
                                                     ;:hsts           true ; add this when you add HTTPS
                                                     :frame-options  :sameorigin
                                                     :xss-protection {:enable? true :mode :block}})))
  (-> api-routes
      (ring-json/wrap-json-response))
  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> combined-routes
      (policy/add-content-security-policy :config-path
                                          "security/policy.clj")
      (policy/wrap-referrer-policy "strict-origin")
      anti-forgery/wrap-anti-forgery
      session/wrap-session))

(defn -main []
  (jetty/run-jetty app
                   {:send-server-version? false
                    :port 8080}))
