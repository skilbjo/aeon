(ns handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :as jetty]
            [controller :as controller])
  (:gen-class))

(defroutes app-routes
  (GET "/" [] (controller/index))
  (route/not-found "<h1>Not Found</h1>"))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)))
  ;(wrap-defaults app-routes site-defaults))

(defn -main []
  (jetty/run-jetty app {:port 8080}))
