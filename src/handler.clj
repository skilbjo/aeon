(ns handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.json :as ring-json ]
            [ring.util.response :refer [response]]
            [ring.adapter.jetty :as jetty]
            [controller :as controller])
  (:gen-class))

(defroutes site-routes
  (GET "/" [] (controller/index)))

(defroutes api-routes
  (GET "/api/data" []
       (response (controller/data)))
  (GET "/api/:dataset" [dataset]
       (response
         (util/printit (controller/query dataset)))))

(defroutes combined-routes
  (-> site-routes
      (ring-defaults/wrap-defaults ring-defaults/site-defaults))
  (-> api-routes
      (ring-json/wrap-json-response))
  (route/not-found "<h1>Not Found</h1>"))

(def app combined-routes)

(defn -main []
  (jetty/run-jetty app {:port 8080}))
