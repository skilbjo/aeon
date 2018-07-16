(ns app.views.register
  (:require [app.views.util :as util]
            [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- register ----------------------------------------------------------------
#_(defn register-user [event registration]
    (.preventDefault event)
    (dispatch [:register-user registration]))

(defn register []
  [:div
   [:p "register"]])

#_(defn register []
    #_(let [default {:username "" :password ""}
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
                  [util/errors-list (:register-user errors)])
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

