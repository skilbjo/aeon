(ns server.util
  (:require [chime :as chime]
            [clj-http.client :as http]
            [clj-time.core :as time]
            [clj-time.format :as formatter]
            [clj-time.periodic :as periodic]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clostache.parser :as clostache]
            [environ.core :refer [env]]
            [markdown.core :as markdown]
            [postal.core :as postal])
  (:import (org.joda.time DateTimeZone)))

; -- dev -----------------------------------------------
(defn print-it [coll]
  (pprint/pprint coll)
  coll)

(defn print-and-die [coll]
  (pprint/pprint coll)
  (System/exit 0))

; -- web -----------------------------------------------
(defn read-template [template-name]
  (-> (str "views/" template-name ".mustache")
      io/resource
      slurp))

(defn render-template [template-file params]
  (clostache/render (read-template template-file)
                    params))

(defn read-markdown [template-name]
  (let [header        (-> (str "views/template/header.md")
                          io/resource
                          slurp)
        body          (-> (str "views/" template-name ".md")
                          io/resource
                          slurp)
        footer        (-> (str "views/template/footer.md")
                          io/resource
                          slurp)]
    (markdown/md-to-html-string (str header body footer))))

(defn render-markdown
  ([template-file]
   (render-markdown template-file {}))
  ([template-file params]
   (clostache/render (read-markdown template-file)
                     params)))

; -- time ----------------------------------------------
(def now (time/now))

(def now' (formatter/unparse (formatter/formatters :date)
                             (time/now)))

(def every-half-hour (-> 30 time/minutes))

(def once-an-hour (-> 1 time/hours))

(def every-six-hours (-> 6 time/hours))

(def once-a-day (-> 1 time/days))

; -- email ---------------------------------------------
(defn send-email [msg]
  #_(when (= 0 (mod @counter 10000)) (log/info "" @counter))
  (let [email (-> :email env)
        cxn   {:host "smtp.gmail.com"
               :ssl  true
               :user email
               :pass (-> :email-pw env)}]
    (postal/send-message cxn {:from    email
                              :to      email
                              :subject (format "Aeon report for %s" now')
                              :body    msg})))

; -- data transformation -------------------------------
(def lower-trim
  (comp string/lower-case string/trim))

; -- collections ---------------------------------------
(defn sequentialize [x]
  (if (sequential? x)
    x
    (vector x)))

(defn multi-line-string [& lines]
  (->> (map sequentialize lines)
       (map string/join)
       (string/join "\n")))

; -- alerts --------------------------------------------
(defn notify-healthchecks-io [api-key]
  (try
    (future (http/get (str "https://hc-ping.com/"
                           api-key)))
    (catch Exception ex
      (log/error ex "Error calling healthchecks.
                     Check API key; or hchk.io status..."))))

(defn schedule-healthchecks-io []
  (let [schedule    (periodic/periodic-seq (-> now
                                               (.withTime 0 0 0 0))
                                           every-half-hour)
        callback-fn (fn [time]
                      (log/debug "Notifying healthchecks.io at " time)
                      (notify-healthchecks-io (env :healthchecks-io-aeon)))]
    (chime/chime-at schedule
                    callback-fn)))
