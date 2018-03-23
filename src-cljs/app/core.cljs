(ns app.core
  (:require [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"}))

(defn main-component []
  (fn []
    [:div ["hello"]
     [:h1 "hello there"]
     [:h2 "yo"]
     [:h3 "more stuff"]
     [:h4 (* 5 4)]
     [:h5 app-state]]))

(defn mount-root []
  (reagent/render [main-component] (.getElementById js/document "app")))

(defn init! []
  (mount-root))

(defn main []
  (init!)
  (println "reloaded!"))

(main)
