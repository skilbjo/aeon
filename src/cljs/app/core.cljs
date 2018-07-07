(ns app.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [app.events]
            [app.subs]
            [app.spec]
            [app.views :as views]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [secretary.core :as secretary])
  (:import goog.History))

;; -- Routes and Navigation ---------------------------------------------------
(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn routes []
  (let [slug    nil
        profile nil]
    (secretary/set-config! :prefix "#")

    (defroute "/" []
        (rf/dispatch [:set-active-page {:page :home}]))
    (defroute "/login" []
        (rf/dispatch [:set-active-page {:page :login}]))
    (defroute "/register" []
        (rf/dispatch [:set-active-page {:page :register}]))

    #_(defroute "/logout" []
        (rf/dispatch [:logout]))
    #_(defroute "/:profile" [profile]
        (rf/dispatch [:set-active-page {:page :profile :profile (subs profile 1)}]))

    #_(hook-browser-navigation!)))

(defn init! []
  (rf/dispatch-sync [:initialize-db])

  (rf/clear-subscription-cache!)

  (routes)

  (reagent/render [views/the-app]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (when goog.DEBUG
    (do (enable-console-print!)
        (js/console.log "we're in dev-mode!")))

  (init!))


(main)
