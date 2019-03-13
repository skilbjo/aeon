(ns app.views
  (:require [app.views.footer :as footer]
            [app.views.header :as header]
            [app.views.login :as login]
            [app.views.portfolio :as portfolio]
            [app.views.report :as report]
            [app.views.register :as register]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as reagent]))

;; -- Home --------------------------------------------------------------------
(defn home []
  [:div
   [:h4 "aeon"]
   [:p "Not much here... go login!"]])

(defn logout []
  [:div
   [:p "logout"]])

(defn pages [page-name]
  (case page-name
    :home        [home]
    :login       [login/login]
    :register    [register/register]
    :portfolio         [report/portfolio]
    :asset-type        [report/asset-type]
    :capitalization    [report/capitalization]
    :investment-style  [report/investment-style]
    :location          [report/location]
    [home]))

(defn send-app []
  (let [active-page @(subscribe [:active-page])]
    [:div
     [header/header]
     [pages active-page]
     [footer/footer]]))
