(ns app.core
  (:require [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(def debug?
  ^boolean goog.DEBUG)

(defn main-component []
  (fn []
    [:div ["hello"]
     [:h1 "hello there"]
     [:h2 "yo"]
     [:h3 "more stuff"]
     [:h4 (* 5 4)] ]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)

  (reagent/render [main-component]
                  (.getElementById js/document "app")))

(defn init! []
  (when debug?
    (do (enable-console-print!)
        (js/console.log "we're in dev-mode!")))

  ;; (re-frame/dispatch-sync [::events/initialize-db])

  (mount-root))

(defn ^:export main []
  (init!))

(main)
