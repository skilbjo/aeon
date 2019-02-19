(ns app.views.header
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn header []
  (let [user        @(subscribe [:user])
        active-page @(subscribe [:active-page])
        anon?       (empty? user)]
    [:nav
     [:div.nav-wrapper
      [:a.brand-logo.center {:href "#/"} "aeon"]
      [:ul#nav-mobile.left
       [:li {:class (when (= active-page :home) "active")}
        [:a {:href "#/"} "Home"]]
       (when anon?
         [:li {:class (when (= active-page :login) "active")}
          [:a {:href "#/login"} "Login"]])
       (when anon?
         [:li {:class (when (= active-page :register) "active")}
          [:a {:href "#/register"} "Register"]])
       (when-not anon?
         [:li {:class (when (= active-page :portfolio) "active")}
          [:a {:href "#/portfolio"}        "Portfolio"]])
       (when-not anon?
         [:li {:class (when (= active-page :asset-type) "active")}
          [:a {:href "#/asset-type"}       "Asset Type"]])
       (when-not anon?
         [:li {:class (when (= active-page :capitalization) "active")}
          [:a {:href "#/capitalization"}   "Capitalization"]])
       (when-not anon?
         [:li {:class (when (= active-page :capitalization) "active")}
          [:a {:href "#/investment-style"} "Investment Style"]])
       (when-not anon?
         [:li {:class (when (= active-page :location) "active")}
          [:a {:href "#/location"}         "Location"]])
       (when-not anon?
         [:li {:class (when (= active-page :logout) "active")}
          [:a {:href "#/logout"} "Logout"]])]]]))
