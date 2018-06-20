(ns app.core
  (:require [app.events]
            [app.subs]
            [app.views :as views]
            [reagent.core :as reagent]
            [re-frame.core :as re-frame]))

(defn init! []
  (re-frame/clear-subscription-cache!)

  (re-frame/dispatch-sync [:initialize-db])

  (reagent/render [views/the-app]
                  (.getElementById js/document "app"))

  ;; (re-frame/dispatch-sync [::events/initialize-db])

  )

(defn ^:export main []
  (when goog.DEBUG
    (do (enable-console-print!)
        (js/console.log "we're in dev-mode!")))

  (init!))

(main)
