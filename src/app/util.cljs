(ns app.util
  (:require [cljs-time.coerce :as coerce]
            [cljs-time.core :as time]
            [cljs-time.format :as formatter]
            [clojure.pprint :as pprint]
            [clojure.string :as string]))

; -- dev -----------------------------------------------
(defn print-it [coll]
  (pprint/pprint coll)
  coll)

; -- time ----------------------------------------------
(defn joda-date->date-str [d]
  (formatter/unparse (formatter/formatters :date)
                     d))

(def now (-> (time/now)
             joda-date->date-str))
