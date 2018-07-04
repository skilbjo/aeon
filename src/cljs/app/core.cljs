(ns app.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [app.events]
            [app.subs]
            [app.spec]
            [app.views :as views]
            [goog.events :as gevents]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]
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

    #_(defroute "/" []
        (dispatch #_[:set-active-page {:page :home}]))
    #_(defroute "/login" []
        (dispatch #_[:set-active-page {:page :login}]))
    #_(defroute "/register" []
        (dispatch #_[:set-active-page {:page :register}]))
    #_(defroute "/logout" []
        (dispatch #_[:logout]))
    #_(defroute "/:profile" [profile]
        (dispatch #_[:set-active-page {:page :profile :profile (subs profile 1)}]))

    (hook-browser-navigation!)))

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
