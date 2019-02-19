(ns app.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [app.events] ;; force compiler to include
            [app.subs]   ;; these namespaces
            [app.spec]
            [app.views :as views]
            [goog.events :as events]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [secretary.core :as secretary])
  (:import (goog History)
           (goog.history EventType)))

;; -- Routes and Navigation ---------------------------------------------------
(def history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn routes []
  (let [slug    nil]
    (secretary/set-config! :prefix "#")

    (defroute "/" []
      (rf/dispatch [:set-active-page {:page :home}]))
    (defroute "/login" []
      (rf/dispatch [:set-active-page {:page :login}]))
    (defroute "/register" []
      (rf/dispatch [:set-active-page {:page :register}]))
    (defroute "/logout" []
      (rf/dispatch [:logout]))
    (defroute "/portfolio" []
      (rf/dispatch [:set-active-page {:page :portfolio}]))
    (defroute "/asset-type" []
      (rf/dispatch [:set-active-page {:page :asset-type}]))
    (defroute "/capitalization" []
      (rf/dispatch [:set-active-page {:page :capitalization}]))
    (defroute "/investment-style" []
      (rf/dispatch [:set-active-page {:page :investment-style}]))
    (defroute "/location" []
      (rf/dispatch [:set-active-page {:page :location}]))))

(defn init! []
  (rf/dispatch-sync [:initialize-db])

  (rf/clear-subscription-cache!)

  (routes)

  (reagent/render [views/send-app]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (when goog.DEBUG
    (do (enable-console-print!)
        (js/console.log "we're in dev-mode!")))

  (init!))

(main)
