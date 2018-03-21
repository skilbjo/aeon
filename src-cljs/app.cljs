(ns app.core
  (:require [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(enable-console-print!)

(defn main []
  (println "fuck"))

(defonce app-state (atom {:text "Hello world!"}))

(defn main-component []
  (fn []
    [:div ["hello"]
     [:h1 "hello sss"]]))

(defn mount-root []
  (reagent/render [main-component] (.getElementById js/document "app")))

(defn init! []
  (do
    (mount-root)
    (println "yoos")))

(init!)
