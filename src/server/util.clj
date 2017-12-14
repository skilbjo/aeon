(ns server.util
  (:require [clj-time.core :as time]
            [clojure.pprint :as pprint]
            [clojure.string :as string]
            [clostache.parser :as clostache]
            [markdown.core :as markdown]
            ;[selmer.parser :as selmer]
            ))


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
    (-> (str header body footer)
        (markdown/md-to-html-string))))

(defn render-markdown
  ([template-file]
   (render-markdown template-file {}))
  ([template-file params]
   (clostache/render (read-markdown template-file)
                     params)))

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
