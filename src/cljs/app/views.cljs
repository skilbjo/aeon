(ns app.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn header []
  [:header#header
   [:h1 "header"]])

(defn footer []
  [:footer#footer
   [:h3 "footer"]
   [:h3 "what the f@! up with these errors"]])

(defn the-app []
  [:div
   [:section#app
    [header]
    (when @(subscribe [:stuffs])
      [:h2 "hey"])]
   [footer]])
