(ns server.util
  (:require [chime :as chime]
            [clj-http.client :as http]
            [clj-time.core :as time]
            [clj-time.format :as formatter]
            [clj-time.periodic :as periodic]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clostache.parser :as clostache]
            [environ.core :refer [env]]
            [markdown.core :as markdown]))

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
      (clojure.java.io/resource)
      slurp))

(defn render-template [template-file params]
  (clostache/render (read-template template-file)
                    params))

(defn read-markdown [template-name]
  (let [header        (-> (str "views/template/header.md")
                          (clojure.java.io/resource)
                          slurp)
        body          (-> (str "views/" template-name ".md")
                          (clojure.java.io/resource)
                          slurp)
        footer        (-> (str "views/template/footer.md")
                          (clojure.java.io/resource)
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

(def once-a-day (-> 1 time/days))

; -- data types ----------------------------------------
(defn string->decimal [n]
  (try
    (BigDecimal. n)
    (catch NumberFormatException e
      n)
    (catch NullPointerException e
      n)))

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
  (http/get (str "https://hchk.io/"
                 api-key)))

(defn schedule-healthchecks.io []
  (let [schedule    (periodic/periodic-seq now
                                           once-a-day)
        callback-fn (fn [time]
                      (println time)
                      (notify-healthchecks-io (env :healthchecks-io-compojure)))]
    (chime/chime-at schedule
                    callback-fn)))
