(ns server.routes
  (:require [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [jobs.static :as jobs.static]
            [jobs.api :as jobs.api]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.json :as ring-json]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.session :as session]
            [ring.util.response :refer [response]])
  (:gen-class))

(defroutes site-routes
  (GET "/old" []
    (jobs.static/index))
  (GET "/" []
    (jobs.static/index-md)))

(defroutes api-routes
  (GET "/api/data" []
    (response (jobs.api/data)))
  (GET "/api/:dataset/latest" [dataset]
    (let [dataset-trusted  (string/escape dataset {\; "" \- ""})]
      (response (jobs.api/data-latest dataset-trusted)))))

(defroutes combined-routes
  (-> site-routes
      (ring-defaults/wrap-defaults ring-defaults/site-defaults))
  (-> api-routes
      (ring-json/wrap-json-response))
  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> combined-routes
      anti-forgery/wrap-anti-forgery
      session/wrap-session))

(defn -main []
  (jetty/run-jetty app {:port 8080}))
