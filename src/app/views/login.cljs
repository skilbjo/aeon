(ns app.views.login
  (:require [app.views.util :as util]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- login -------------------------------------------------------------------
(defn login-user [event credentials]
  (.preventDefault event)
  (dispatch [:login credentials]))

(defn login []
  (let [default     {:user "" :password ""}
        credentials (reagent/atom default)]
    (fn []
      (let [user     (get @credentials :user)
            password (get @credentials :password)
            errors   @(subscribe [:errors])
            loading  @(subscribe [:loading])]
        [:div.container
         [:div.row
          [:div.col.s6.offset-s3.z-depth-1
           [:h5#title "login form"]
           (when (:login errors)
             [util/errors-list (:login errors)])
           [:form {:on-submit #(login-user % @credentials)}
            [:div#username.input-field
             [:input.validate {:type        "text"
                               :placeholder "user"
                               :value       user
                               :on-change   #(swap! credentials assoc
                                                    :user
                                                    (-> % .-target .-value))
                               :disabled    (when (:login loading))}]
             #_[:label {:for "username"} "username"]]
            [:div#password.input-field
             [:input.validate {:type        "text"
                               :placeholder "password"
                               :value       password
                               :on-change   #(swap! credentials assoc
                                                    :user
                                                    (-> % .-target .-value))
                               :disabled    (when (:login loading))}]
             [:label {:for "password"} "password"]]
            [:p
             [:input#remember {:type "checkbox"}]
             [:label#checkbox {:for "remember"} "Remember me"]]
            [:a#loginbtn.waves-effect.waves-light.btn
             {:class (when (:login loading) "disabled")} "Login"]
            #_[:button {:class (when (:login loading) "disabled")}
               "Sign in"]]]]]))))

