(ns app.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn header-old []
  [:header#header
   [:h1 "headerr"]])

(defn header []
  (let [user        @(subscribe [:user])
        active-page @(subscribe [:active-page])]
    [:nav.navbar.navbar-light
     [:div.container
      [:a.navbar-brand {:href "#/"} "aoin"]
      (if (empty? user)
        (do
          (println "empty")
          [:ul.nav.navbar-nav.pull-xs-right
           [:li.nav-item
            [:a.nav-link {:href "#/" :class (when (= active-page :home) "active")} "Home"]]
           [:li.nav-item
            [:a.nav-link {:href "#/login" :class (when (= active-page :login) "active")} "Sign in"]]
           [:li.nav-item
            [:a.nav-link {:href "#/register" :class (when (= active-page :register) "active")} "Sign up"]]])
        (do
          (println "not empty")
          [:ul.nav.navbar-nav.pull-xs-right
           [:li.nav-item
            [:a.nav-link {:href "#/" :class (when (= active-page :home) "active")} "Home"]]
           [:li.nav-item
            [:a.nav-link {:href "#/editor" :class (when (= active-page :editor) "active")}
             [:i.ion-compose "New Article"]]]
           [:li.nav-item
            [:a.nav-link {:href "#/settings" :class (when (= active-page :settings) "active")}
             [:i.ion-gear-a "Settings"]]]
           [:li.nav-item
            [:a.nav-link {:href (str "#/@" (:username user)) :class (when (= active-page :profile) "active")} (:username user)
             [:img.user-pic {:src (:image user)}]]]]))]]))

(defn footer []
  [:footer#footer
   [:h3 "footer"]
   [:h3 "what the f@! up with these errors"]])

;; -- Register ----------------------------------------------------------------
(defn register-user [event registration]
  (.preventDefault event)
  #_(dispatch [:register-user registration]))

(defn register []
  #_(let [default {:username "" :email "" :password ""}
          registration (reagent/atom default)]
      (fn []
        (let [username (get @registration :username)
              email (get @registration :email)
              password (get @registration :password)
              loading @(subscribe [:loading])
              errors @(subscribe [:errors])]
          [:div.auth-page
           [:div.container.page
            [:div.row
             [:div.col-md-6.offset-md-3.col-xs-12
              [:h1.text-xs-center "Sign up"]
              [:p.text-xs-center
               [:a {:href "#/login"} "Have an account?"]]
              (when (:register-user errors)
                [errors-list (:register-user errors)])
              [:form {:on-submit #(register-user % @registration)}
               [:fieldset.form-group
                [:input.form-control.form-control-lg {:type "text"
                                                      :placeholder "Your Name"
                                                      :value username
                                                      :on-change #(swap! registration assoc :username (-> % .-target .-value))
                                                      :disabled (when (:register-user loading))}]]
               [:fieldset.form-group
                [:input.form-control.form-control-lg {:type "text"
                                                      :placeholder "Email"
                                                      :value email
                                                      :on-change #(swap! registration assoc :email (-> % .-target .-value))
                                                      :disabled (when (:register-user loading))}]]
               [:fieldset.form-group
                [:input.form-control.form-control-lg {:type "password"
                                                      :placeholder "Password"
                                                      :value password
                                                      :on-change #(swap! registration assoc :password (-> % .-target .-value))
                                                      :disabled (when (:register-user loading))}]]
               [:button.btn.btn-lg.btn-primary.pull-xs-right {:class (when (:register-user loading) "disabled")} "Sign up"]]]]]]))))

;; -- Profile -----------------------------------------------------------------
;;
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

(defn the-app []
  [:div
   [header]
   [:section#app
    (when @(subscribe [:stuffs])
      [:h2 "hey"])]
   [footer]])
