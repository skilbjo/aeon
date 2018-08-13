(ns app.events
  (:require [app.db :as db]
            [app.util :as util]
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
  (let [api-url "/api/v1"]
    (println "endpoint is: " (string/join "/" (concat [api-url] params)))
    (string/join "/" (concat [api-url] params))))

(defn auth-header [db]
  "Get user token and format for API authorization"
  (let [token (-> db :user :token)]
    (println "auth-header called. token is: " token)
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
        ;; -- URL @ "/login" | "/register" ---------------------------
                (:home :login :register) {:db set-page}
        ;; -- URL @ "/" ----------------------------------------------
                :portfolio {:dispatch [:portfolio]}))))

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
            (let [user'  (-> response first :user)
                  token (-> response first :token)]
              {:db (assoc user
                          :user   user'
                          :token  token)
               :dispatch-n (list [:complete-request :login]
                                 [:set-active-page {:page :home}])})))

(rf/reg-event-fx
 :logout
 remove-user-interceptor
 (fn-traced [{:keys [db]} _]
            {:db      (dissoc db
                              :user
                              :portfolio
                              :loading
                              :errors
                              :re-frame-datatable.core/re-frame-datatable)
             :dispatch [:set-active-page {:page :home}]}))

;; -- GET Portfolio @ api/v1/reports/portfolio --------------------------------
(rf/reg-event-fx
 :portfolio
 (fn-traced [{:keys [db]} [_ body]]
            (let [user     (-> db :user :user)
                  password (-> db :user :token)]
              {:db         (assoc-in db [:loading :portfolio] true)
               :http-xhrio {:method          :get
                            :uri             (endpoint "reports" "portfolio")
                            :headers         (auth-header db)
                            :params          {:user     user
                                              :password password}
                            :format          (json-request-format)
                            :response-format (json-response-format
                                              {:keywords? true})
                            :on-success      [:portfolio-success]
                            :on-failure      [:api-request-error :portfolio]}})))

(rf/reg-event-fx
 :portfolio-success
 (fn-traced [{:keys [db]} [_ {result :body}]]
            {:db (-> db
                     (assoc-in [:loading :portfolio] false)
                     (assoc :active-page :portfolio)
                     (assoc :portfolio result))
             :dispatch-n (list [:complete-request :portfolio])}))

;; -- Request Handlers -----------------------------------------------------------
(rf/reg-event-db
 :complete-request
 (fn-traced [db [_ request-type]]
            (assoc-in db
                      [:loading request-type] false)))

(rf/reg-event-fx
 :api-request-error
 (fn-traced [{:keys [db]} [_ request-type response]]
            {:db (-> db
                     (assoc-in [:errors request-type]
                               (-> response :status-text)))
             :dispatch [:complete-request request-type]}))
