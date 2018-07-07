(ns app.events
  (:require [app.db :as db]
            [app.spec :as s]
            [cljs.spec.alpha :as spec]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]))

(defn auth-header [db]
  "Get user token and format for API authorization"
  (let [token (get-in db [:user :token])]
    (if token
      [:Authorization (str "Token " token)]
      nil)))

(rf/reg-event-fx  ;; usage: (dispatch [:initialise-db])
  :initialize-db  ;; sets up initial application state
  [(rf/inject-cofx :local-store-user) s/check-spec-interceptor]
  (fn-traced [{:keys [db local-store-user]} _]
             {:db (-> db/default-db
                      (assoc :stuffs local-store-user))}))

(rf/reg-event-fx    ;; usage: (dispatch [:set-active-page {:page :home})
  :set-active-page  ;; when user clicks on a link to go to a another page
  (fn-traced [{:keys [db]} [_ {:keys [page    ;; destructure 2nd parameter
                                      slug    ;; to obtain keys
                                      profile
                                      favorited]}]]
             (let [set-page (assoc db :active-page page)]
               (case page
                 ;; -- URL @ "/" --------------------------------------------------------
                 :home {:db set-page}

                 ;; -- URL @ "/login" | "/register" -------------------------------------
                 (:login :register) {:db set-page}
                 ;; -- URL @ "/:profile" ------------------------------------------------
                 ))))
