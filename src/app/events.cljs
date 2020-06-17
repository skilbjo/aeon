(ns app.events
  (:require-macros [cljs-log.core :as log])
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

(def backend-uri "https://skilbjo.duckdns.org")

(defn endpoint [& params]
  (let [api-prefix       "api/v1"
        prefix           (str backend-uri "/" api-prefix)   ;; comment out #_backend-uri if want to test locally
        joined-endpoint  (string/join "/" (concat [prefix] params))]
    (log/debug "endpoint is: " joined-endpoint)
    joined-endpoint))

(defn auth-header [db]
  "Get user token and format for API authorization"
  (let [token (-> db :user :token)]
    (log/debug "auth-header called. token is: " token)
    (if token
      [:Authorization (str "Token " token)]
      nil)))

(defn cors-header []
  [:Access-Control-Allow-Origin backend-uri])

(defn dispatch-report [db body report]
  (let [user     (-> db :user :user)
        password (-> db :user :token)
        report-cached? (some? (util/get-report db report))]
    (if report-cached?
      {:db (-> db ; this is the body of the report-success fn
               (assoc-in [:loading (keyword report)] false)
               (assoc :active-page (keyword report)))
       :dispatch-n (list [:complete-request (keyword report)])}
      {:db         (assoc-in db [:loading (-> report keyword)] true)
       :http-xhrio {:method          :get
                    :uri             (endpoint "reports" report)
                    :headers         (auth-header db)
                    :params          {:user     user
                                      :password password}
                    :format          (json-request-format)
                    :response-format (json-response-format
                                      {:keywords? true})
                    :on-success      [(-> report name (str "-success") keyword)]
                    :on-failure      [:api-request-error (keyword report)]}})))

(defn report-success [db result report]
  {:db (-> db
           (assoc-in [:loading (keyword report)] false)
           (assoc :active-page (keyword report))
           (assoc (keyword report) result))
   :dispatch-n (list [:complete-request (keyword report)])})

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
        ;; -- URL @ "/" | "/login" | "/register" ---------------------
                (:home :login :register) {:db set-page}
        ;; -- URL @ "/portfolio" -------------------------------------
                :portfolio {:dispatch [:portfolio]}
        ;; -- URL @ "/asset-type" ------------------------------------
                :asset-type {:dispatch [:asset-type]}
        ;; -- URL @ "/investment-style" ------------------------------
                :investment-style {:dispatch [:investment-style]}
        ;; -- URL @ "/capitalization" ---------------------------------
                :capitalization {:dispatch [:capitalization]}
        ;; -- URL @ "/location" ---------------------------------------
                :location {:dispatch [:location]}))))

;; -- POST Login @ /api/login -------------------------------------------------
(rf/reg-event-fx
 :login
 (fn-traced [{:keys [db]} [_ body]]
            {:db         (assoc-in db [:loading :login] true)
             :http-xhrio {:method          :post
                          :uri             (endpoint "login")
                          :headers         (vector
                                             (auth-header db)
                                             (cors-header))
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
                                 [:set-active-page {:page :home}])
               :set-hash {:hash "/"}})))

(rf/reg-event-fx
 :logout
 remove-user-interceptor
 (fn-traced [{:keys [db]} _]
            {:db      (dissoc db
                              :user
                              :portfolio
                              :asset-type
                              :capitalization
                              :investment-style
                              :location
                              :loading
                              :errors
                              :re-frame-datatable.core/re-frame-datatable)
             :dispatch [:set-active-page {:page :home}]
             :set-hash {:hash "/"}}))

;; -- GET Portfolio @ api/v1/reports/portfolio --------------------------------
(rf/reg-event-fx
 :portfolio
 (fn-traced [{:keys [db]} [_ body]]
            (dispatch-report db body "portfolio")))

(rf/reg-event-fx
 :portfolio-success
 (fn-traced [{:keys [db]} [_ {result :body}]]
            (report-success db result "portfolio")))

;; -- GET Asset-type @ api/v1/reports/asset-type ------------------------------
(rf/reg-event-fx
 :asset-type
 (fn-traced [{:keys [db]} [_ body]]
            (dispatch-report db body "asset-type")))

(rf/reg-event-fx
 :asset-type-success
 (fn-traced [{:keys [db]} [_ {result :body}]]
            (report-success db result "asset-type")))

;; -- GET Asset-type @ api/v1/reports/capitalization --------------------------
(rf/reg-event-fx
 :capitalization
 (fn-traced [{:keys [db]} [_ body]]
            (dispatch-report db body "capitalization")))

(rf/reg-event-fx
 :capitalization-success
 (fn-traced [{:keys [db]} [_ {result :body}]]
            (report-success db result "capitalization")))

;; -- GET Asset-type @ api/v1/reports/investment-style ------------------------
(rf/reg-event-fx
 :investment-style
 (fn-traced [{:keys [db]} [_ body]]
            (dispatch-report db body "investment-style")))

(rf/reg-event-fx
 :investment-style-success
 (fn-traced [{:keys [db]} [_ {result :body}]]
            (report-success db result "investment-style")))

;; -- GET Asset-type @ api/v1/reports/location --------------------------------
(rf/reg-event-fx
 :location
 (fn-traced [{:keys [db]} [_ body]]
            (dispatch-report db body "location")))

(rf/reg-event-fx
 :location-success
 (fn-traced [{:keys [db]} [_ {result :body}]]
            (report-success db result "location")))

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
