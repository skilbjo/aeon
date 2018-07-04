(ns app.events
  (:require [app.db :as db]
            [app.spec :as s]
            [cljs.spec.alpha :as spec]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as re-frame]
            #_[re-frame.core :refer [reg-event-fx
                                     inject-cofx
                                     path
                                     after]]))

#_(def todo-interceptors [s/check-spec-interceptor
                          (path :user)
                          db/->local-store])

(defn auth-header [db]
  "Get user token and format for API authorization"
  (let [token (get-in db [:user :token])]
    (if token
      [:Authorization (str "Token " token)]
      nil)))

(re-frame/reg-event-fx  ;; usage: (dispatch [:initialise-db])
 :initialize-db         ;; sets up initial application state
 [(re-frame/inject-cofx :local-store-user) s/check-spec-interceptor]
 (fn-traced [{:keys [db local-store-user]} _]
            {:db (-> db/default-db
                     (assoc :stuffs local-store-user))}))

#_(reg-event-fx      ;; usage: (dispatch [:set-active-page {:page :home})
   :set-active-page  ;; triggered when the user clicks on a link that redirects to a another page
   (fn-traced [{:keys [db]} [_ {:keys [page slug profile favorited]}]]  ;; destructure 2nd parameter to obtain keys
              (let [set-page (assoc db :active-page page)]
                (case page
       ;; -- URL @ "/" --------------------------------------------------------
                  :home {:db set-page
                         :dispatch-n  (list (if (empty? (:user db))  ;; dispatch more than one event. When a user
                                              [:get-articles {:limit 10}]        ;; is NOT logged in we display all articles
                                              [:get-feed-articles {:limit 10}])  ;; otherwiser we get her/his feed articles
                                            [:get-tags])}            ;; we also can't forget to get tags

       ;; -- URL @ "/login" | "/register" | "/settings" -----------------------
                  (:login :register :settings) {:db set-page}  ;; when using case with multiple test constants that
                                                    ;; do the same thing we can group them together
                                                    ;; (:login :register :settings) {:db set-page} is the same as:
                                                    ;; :login {:db set-page}
                                                    ;; :register {:db set-page}
                                                    ;; :settings {:db set-page}
       ;; -- URL @ "/:profile" ------------------------------------------------
                  :profile {:db         (assoc set-page
                                               :active-article slug) ;; again for dispatching multiple
                            :dispatch-n (list [:get-user-profile {:profile profile}] ;; events we can use :dispatch-n
                                              [:get-articles {:author profile}])}))))
