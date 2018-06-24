(ns app.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [app.events]
            [app.subs]
            [app.spec]
            [app.views :as views]))

(defn init! []
  (re-frame/clear-subscription-cache!)

  (reagent/render [views/the-app]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (when goog.DEBUG
    (do (enable-console-print!)
        (js/console.log "we're in dev-mode!")))

  (init!))

(re-frame/dispatch-sync [:initialize-db])
(main)
