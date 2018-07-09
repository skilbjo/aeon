(ns app.views
  (:require [app.views.footer :as footer]
            [app.views.header :as header]
            [app.views.login :as login]
            [app.views.register :as register]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as reagent]))

;; -- profile -----------------------------------------------------------------
(defn profile []
  #_(let [profile @(subscribe [:profile])
          filter @(subscribe [:filter])
          loading @(subscribe [:loading])
          articles @(subscribe [:articles])
          user @(subscribe [:user])]
      [:div.profile-page
       [:div.user-info
        [:div.container
         [:div.row
          [:div.col-xs-12.col-md-10.offset-md-1
           [:img.user-img {:src (:image profile)}]
           [:h4 (:username profile)]
           [:p (:bio profile)]
           (if (= (:username user) (:username profile))
             [:a.btn.btn-sm.btn-outline-secondary.action-btn {:href "#/settings"}
              [:i.ion-gear-a] " Edit Profile Settings"]
             [:button.btn.btn-sm.action-btn.btn-outline-secondary {:on-click #(dispatch [:toggle-follow-user (:username profile)])
                                                                   :class (when (:toggle-follow-user loading) "disabled")}
              [:i {:class (if (:following profile) "ion-minus-round" "ion-plus-round")}]
              [:span (if (:following profile) (str " Unfollow " (:username profile)) (str " Follow " (:username profile)))]])]]]]
       [:div.container
        [:row
         [:div.col-xs-12.col-md-10.offset-md-1
          [:div.articles-toggle
           [:ul.nav.nav-pills.outline-active
            [:li.nav-item
             [:a.nav-link {:href (str "#/@" (:username profile)) :class (when (:author filter) " active")} "My Articles"]]
            [:li.nav-item
             [:a.nav-link {:href (str "#/@" (:username profile) "/favorites") :class (when (:favorites filter) "nav-link active")} "Favorited Articles"]]]]
          [articles-list articles (:articles loading)]]]]]))

;; -- Home --------------------------------------------------------------------
(defn home []
  [:div
   [:h4 "Aoin"]
   [:p "Not much here... go login!"]])

(defn register []
  [:div
   [:p "register"]])

(defn portfolio []
  [:div
   [:p "portfolio"]])

(defn logout []
  [:div
   [:p "logout"]])

(defn pages [page-name]
  (case page-name
    :home     [home]
    :login    [login/login]
    :register [register/register]
    [home]))

(defn send-app []
  (let [active-page @(subscribe [:active-page])]
    [:div
     [header/header]
     [pages active-page]
     [footer/footer]]))
