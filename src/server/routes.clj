(ns server.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [jobs.static :as jobs.static]
            [jobs.api :as jobs.api]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.json :as ring-json]
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
    (response (jobs.api/data-latest dataset))))

(defroutes combined-routes
  (-> site-routes
      (ring-defaults/wrap-defaults ring-defaults/site-defaults))
  (-> api-routes
      (ring-json/wrap-json-response))
  (route/not-found "<h1>Not Found</h1>"))

(def app combined-routes)

(defn -main []
  (jetty/run-jetty app {:port 8080}))
