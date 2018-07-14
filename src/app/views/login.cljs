(ns app.views.login
  (:require [app.views.util :as util]
            [reagent.core :as reagent]
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
             [util/error (:login errors)])
           [:form.col.s12 {:on-submit #(login-user % @credentials)}
            [:div.input-field.col.s6
             [:input#username.validate {:type        "text"
                                        :placeholder "user"
                                        :value       user
                                        :disabled    (when (:login loading))
                                        :on-change   #(swap! credentials assoc
                                                             :user
                                                             (-> %
                                                                 .-target
                                                                 .-value))}]
             [:label {:for "username"} "username"]]
            [:div.input-field.col.s6
             [:input#password.validate {:type        "password"
                                        :placeholder "password"
                                        :value       password
                                        :disabled    (when (:login loading))
                                        #_:on-change #_(fn [c]
                                                         (println (:user @credentials))
                                                         (swap! credentials assoc
                                                                :password
                                                                (-> c
                                                                    .-target
                                                                    .-value)))
                                        :on-change   #(swap! credentials assoc
                                                             :password
                                                             (-> %
                                                                 .-target
                                                                 .-value))}]
             [:label {:for "password"} "password"]]
            [:button.waves-effect.waves-light.btn
             {:class (when (:login loading) "disabled")}
             "Sign in"]]]]]))))
