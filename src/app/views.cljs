(ns app.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn header-old []
  [:header#header
   [:h1 "headerr"]])

(defn header []
  (let [user        @(subscribe [:user])
        active-page @(subscribe [:active-page])]
    [:nav
     [:div.nav-wrapper
      [:a.brand-logo {:href "#/"} "aoin"]
      [:ul#nav-mobile.right.hide-on-med-and-down
       [:li [:a {:href  "#/"
                 :class (when (= active-page :home) "active")}
             "Home"]]
       (if (empty? user)
         (do
           [:li [:a {:href  "#/"
                     :class (when (= active-page :home) "active")}
                 "Home"]]
           [:li [:a {:href  "#/login"
                     :class (when (= active-page :login) "active")}
                 "Login"]]
           [:li [:a {:href  "#/register"
                     :class (when (= active-page :register) "active")}
                 "Register"]])
         (do
           [:li [:a {:href  "#/"
                     :class (when (= active-page :home) "active")}
                 "Home"]]
           [:li [:a {:href  "#/logout"
                     :class (when (= active-page :home) "active")}
                 "Logout"]]))]]]))

(defn footer []
  [:footer#footer
   [:h3 "footer"]
   [:h3 "what the f@! up with these errors"]])

;; -- Register ----------------------------------------------------------------
(defn register-user [event registration]
  (.preventDefault event)
  #_(dispatch [:register-user registration]))

#_(defn register []
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

;; -- Home --------------------------------------------------------------------
;;

(defn home []
  [:div.home-page
   [:p "home"]])

(defn login []
  [:div.home-page
   [:p "login"]])

(defn register []
  [:div.home-page
   [:p "register"]])

#_(defn home []
    (let [filter @(subscribe [:filter])
          tags @(subscribe [:tags])
          loading @(subscribe [:loading])
          articles @(subscribe [:articles])
          articles-count @(subscribe [:articles-count])
          user @(subscribe [:user])]
      [:div.home-page
       (when (empty? user)
         [:div.banner
          [:div.container
           [:h1.logo-font "conduit"]
           [:p "A place to share your knowledge."]]])
       [:div.container.page
        [:div.row
         [:div.col-md-9
          [:div.feed-toggle
           [:ul.nav.nav-pills.outline-active
            (when-not (empty? user)
              [:li.nav-item
               [:a.nav-link {:href ""
                             :class (when (:feed filter) "active")
                             :on-click #(get-feed-articles % {:offset 0 :limit 10})} "Your Feed"]])
            [:li.nav-item
             [:a.nav-link {:href ""
                           :class (when-not (or (:tag filter) (:feed filter)) "active")
                           :on-click #(get-articles % {:offset 0 :limit 10})} "Global Feed"]] ;; first argument: % is browser event, second: map of filter params
            (when (:tag filter)
              [:li.nav-item
               [:a.nav-link.active
                [:i.ion-pound] (str " " (:tag filter))]])]]
          [articles-list articles (:articles loading)]
          (when-not (or (:articles loading) (< articles-count 10))
            [:ul.pagination
             (for [offset (range (/ articles-count 10))]
               ^{:key offset} [:li.page-item {:class (when (= (* offset 10) (:offset filter)) "active")
                                              :on-click #(get-articles % {:offset (* offset 10) :tag (:tag filter) :limit 10})}
                               [:a.page-link {:href ""} (+ 1 offset)]])])]

         [:div.col-md-3
          [:div.sidebar
           [:p "Popular Tags"]
           (if (:tags loading)
             [:p "Loading tags ..."]
             [:div.tag-list
              (for [tag tags]
                ^{:key tag} [:a.tag-pill.tag-default {:href ""
                                                      :on-click #(get-articles % {:tag tag :limit 10 :offset 0})} tag])])]]]]]))

#_(defn the-app []
    [:div
     [header]
     [:section#app
      (when @(subscribe [:stuffs])
        [:h2 "hey"])]
     [footer]])

(defn pages [page-name]
  (case page-name
    :home     [home]
    :login    [login]
    :register [register]
    [home]))

(defn the-app []
  (let [active-page @(subscribe [:active-page])]
    [:div
     [header]
     [pages active-page]
     [footer]]))
