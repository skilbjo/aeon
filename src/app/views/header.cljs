(ns app.views.header
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn header []
  (let [user        @(subscribe [:user])
        active-page @(subscribe [:active-page])
        anon?       (empty? user)]
    [:nav
     [:div.nav-wrapper
      [:a.brand-logo.center {:href "#/"}
       [:i.material-icons "cloud"]
       "aoin"]
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
          [:a {:href "#/logout"} "Portfolio"]]
         [:li {:class (when (= active-page :logout) "active")}
          [:a {:href "#/logout"} "Logout"]])]]]))
