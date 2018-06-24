(ns app.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn header []
  [:header#header
   [:h1 "header"]])

(defn footer []
  [:footer#footer
   [:h3 "footer"]])

(defn the-app []
  [:div
   [:section#app
    [header]
   #_(when (seq @(subscribe [:stuffs]))
      [:h2 "hey"])]
   [footer]])
