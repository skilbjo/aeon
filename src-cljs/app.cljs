(ns app.core
  (:require [cljsjs.jquery]
            #_[reagent.core :as reagent]
            #_[re-frame.core :as re-frame]))

(enable-console-print!)

(println "fuck")

(js/console.log "fuck")
(js/alert "fuck")

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
    (js/alert "fuck")
    (.attach js/FastClick (.-body js/document))))

(init!)
(main)
