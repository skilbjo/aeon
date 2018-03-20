(ns compojure-app
  (:require [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(enable-console-print!)

(println "fuck")

(js/console.log "fuck")

(defn main []
  (println "fuck"))

(defonce app-state (atom {:text "Hello world!"}))

(.initializeTouchEvents js/React true)

(defn main-component []
  (fn []
    [:div ["hello"]
     [:h1 ["hello"]]]))

(defn mount-root []
  #_(reagent/render [main-component] (.getElementById js/document "app")))

(defn init! []
  (do
    (mount-root)
    (println "fuck")
    (.attach js/FastClick (.-body js/document))))

(init!)

