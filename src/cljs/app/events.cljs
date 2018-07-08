(ns app.events
  (:require [app.db :as db]
            [app.spec :as s]
            [cljs.spec.alpha :as spec]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [ajax.core :refer [json-request-format json-response-format]]
            [re-frame.core :as rf]))

(def set-user-interceptor [(rf/path :user)         ;; `:user` path within `db`, rather than the full `db`.
                           (rf/after db/set-user-ls)  ;; write user to localstore (after)
                           rf/trim-v])             ;; removes first (event id) element from the event vec

(def remove-user-interceptor [(rf/after db/remove-user-ls)])

(defn endpoint [& params]
  (let [api-url "localhost:8081/cljs"]
    (string/join "/" (concat [api-url] params))))

(defn auth-header [db]
  "Get user token and format for API authorization"
  (let [token (get-in db [:user :token])]
    (if token
      [:Authorization (str "Token " token)]
      nil)))

(rf/reg-event-fx  ;; usage: (dispatch [:initialise-db])
  :initialize-db  ;; sets up initial application state
  [(rf/inject-cofx :local-store-user) #_s/check-spec-interceptor]
  (fn-traced [{:keys [db local-store-user]} _]
             {:db (-> db/default-db
                      (assoc :user local-store-user))}))

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

(rf/reg-event-fx                        ;; usage (dispatch [:login user])
 :login                              ;; triggered when a users submits login form
 (fn-traced [{:keys [db]} [_ credentials]]  ;; credentials = {:email ... :password ...}
            {:db         (assoc-in db [:loading :login] true)
             :http-xhrio {:method          :post
                          :uri             (endpoint "users" "login")                ;; evaluates to "api/users/login"
                          :params          {:user credentials}                       ;; {:user {:email ... :password ...}}
                          :format          (json-request-format)                     ;; make sure it's json
                          :response-format (json-response-format {:keywords? true})  ;; json response and all keys to keywords
                          :on-success      [:login-success]                          ;; trigger login-success
                          :on-failure      [:api-request-error :login]}}))           ;; trigger api-request-error with :login

(rf/reg-event-fx
 :login-success
 ;; The standard set of interceptors, defined above, which we
 ;; use for all user-modifying event handlers. Looks after
 ;; writing user to localStorage.
 ;; NOTE: this chain includes `path` and `trim-v`
 set-user-interceptor

 ;; The event handler function.
 ;; The "path" interceptor in `set-user-interceptor` means 1st parameter is the
 ;; value at `:user` path within `db`, rather than the full `db`.
 ;; And, further, it means the event handler returns just the value to be
 ;; put into `:user` path, and not the entire `db`.
 ;; So, a path interceptor makes the event handler act more like clojure's `update-in`
 (fn-traced [{user :db} [{props :user}]]
            {:db (merge user props)}))

(rf/reg-event-fx  ;; usage (dispatch [:logout])
 :logout
 remove-user-interceptor
 (fn-traced [{:keys [db]} _]
            {:db      (dissoc db :user)
             :dispatch [:set-active-page {:page :home}]}))

