(ns app.events
  (:require [app.db :as db]
            [ajax.core :refer [json-request-format json-response-format]]
            [clojure.string :as string]
            [day8.re-frame.http-fx] ;; :http-xhrio self-register with re-frame
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [re-frame.core :as rf]))

;; -- utils -------------------------------------------------------------------
(def set-user-interceptor [(rf/path :user)
                           (rf/after db/set-user-ls)
                           rf/trim-v])

(def remove-user-interceptor [(rf/after db/remove-user-ls)])

(defn endpoint [& params]
  (let [api-url "http://localhost:8081/api/v1"]
    (string/join "/" (concat [api-url] params))))

(defn auth-header [db]
  "Get user token and format for API authorization"
  (let [token (get-in db [:user :token])]
    (if token
      [:Authorization (str "Token " token)]
      nil)))

(rf/reg-fx
 :set-hash
 (fn [{:keys [hash]}]
   (set! (.-hash js/location) hash)))

;; -- init --------------------------------------------------------------------
(rf/reg-event-fx  ;; usage: (dispatch [:initialise-db])
 :initialize-db
 [(rf/inject-cofx :local-store-user) #_s/check-spec-interceptor]
 (fn-traced [{:keys [db local-store-user]} _]
            {:db (-> db/default-db
                     (assoc :user local-store-user))}))

(rf/reg-event-fx    ;; usage: (dispatch [:set-active-page {:page :home})
 :set-active-page   ;; when user clicks on a link to go to a another page
 (fn-traced [{:keys [db]} [_ {:keys [page    ;; destructure 2nd parameter
                                     slug    ;; to obtain keys
                                     profile]}]]
            (let [set-page (assoc db :active-page page)]
              (case page
        ;; -- URL @ "/" ----------------------------------------------
                :home {:db set-page}
        ;; -- URL @ "/login" | "/register" ---------------------------
                (:login :register) {:db set-page}))))

;; -- POST Login @ /api/login -------------------------------------------------
(rf/reg-event-fx
 :login
 (fn-traced [{:keys [db]} [_ body]]
            {:db         (assoc-in db [:loading :login] true)
             :http-xhrio {:method          :post
                          :uri             (endpoint "login")
                          :headers         (auth-header db)
                          :params          body
                          :format          (json-request-format)
                          :response-format (json-response-format
                                            {:keywords? true})
                          :on-success      [:login-success]
                          :on-failure      [:api-request-error :login]}}))

(rf/reg-event-fx
 :login-success
 set-user-interceptor
 (fn-traced [{user :db} response]
            #_[:Authorization (str "Token " token)]
            (let [token (-> response first :token)]
              {:db (assoc user
                          :auth [:Authorization (str "Token " token)])})))

(rf/reg-event-fx
 :logout
 remove-user-interceptor
 (fn-traced [{:keys [db]} _]
            {:db      (dissoc db :user)
             :dispatch [:set-active-page {:page :home}]}))

;; -- POST Registration @ /api/user -------------------------------------------
(rf/reg-event-fx
 :register-user
 (fn [{:keys [db]} [_ registration]]
   {:db         (assoc-in db [:loading :register-user] true)
    :http-xhrio {:method          :post
                 :uri             (endpoint "users")
                 :params          {:user registration}
                 :format          (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success      [:register-user-success]
                 :on-failure      [:api-request-error :register-user]}}))

(rf/reg-event-fx
 :register-user-success
 set-user-interceptor
 (fn [{user :db} [{props :user}]]
   {:db (merge user props)
    :dispatch [:complete-request :register-user]
    :set-hash {:hash "/"}}))

;; -- Request Handlers -----------------------------------------------------------
(rf/reg-event-db
 :complete-request
 (fn-traced [db [_ request-type]]
            (assoc-in db
                      [:loading request-type] false)))

(rf/reg-event-fx
 :api-request-error
 (fn-traced [{:keys [db]} [_ request-type response]]
            {:db (assoc-in db
                           [:errors request-type]
                           (-> response :status-text))
             :dispatch [:complete-request request-type]}))
