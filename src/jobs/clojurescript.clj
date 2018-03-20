(ns jobs.clojurescript
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]
            [environ.core :refer [env]]
            [server.util :as util]))

(def title
  "skilbjo + clojurescript")

(def app
  (html
    [:html
     [:head
      [:title title]
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport"
              :content "width=device-width, user-scalable=no, initial-scale=1, minimum-scale=1, maximum-scale=1"}]
      [:meta {:name "apple-mobile-web-app-capable" :content "yes"}]
      [:meta {:name "mobile-web-app-capable" :content "yes"}]
      [:meta {:name "apple-mobile-web-app-title" :content title}]
      [:meta {:name "apple-mobile-web-app-status-bar-style" :content "black-translucent"}]

      (include-css "https://fonts.googleapis.com/icon?family=Material+Icons"
                   "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/css/materialize.min.css"
                   "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css")]
     [:body
      [:div#app]
      (include-js "js/app.js"
                  "https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"
                  "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.0/js/materialize.min.js")]]))

(defn send-app []
  app)
