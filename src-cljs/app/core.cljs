(ns app.core
  (:require [cljsjs.jquery]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn main-component []
  (fn []
    [:div ["hello"]
     [:h1 "hello there"]
     [:h2 "yo"]
     [:h3 "more stuff"]
     [:h4 (* 5 4)] ]))

(defn init! []
  (re-frame/clear-subscription-cache!)

  (re-frame/dispatch-sync [:initialize-db])

  (reagent/render [main-component]
                  (.getElementById js/document "app"))

  ;; (re-frame/dispatch-sync [::events/initialize-db])

  )

(defn ^:export main []
  (when goog.DEBUG
    (do (enable-console-print!)
        (js/console.log "we're in dev-mode!")))

  (init!))

(main)
