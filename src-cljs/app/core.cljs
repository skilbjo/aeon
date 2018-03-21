(ns app.core
  (:require [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defonce app-state (atom {:text "Hello world!"}))

(enable-console-print!)

(defn main-component []
  (fn []
    [:div ["hello"]
     [:h1 "hello there"]
     [:h2 "yo"]
     [:h3 "more stuff"]
     [:h4 (* 5 5)]]))

(defn mount-root []
  (reagent/render [main-component] (.getElementById js/document "app")))

(defn init! []
  (do (mount-root)
      (println "yoas")))

(defn main []
  (init!)
  (println "fuck"))

(main)
