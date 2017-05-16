(ns controller
  (:require [sql :as sql]
            [util :as util]))

; Main
(defn index []
  (util/render-template "index"
                        {:name "clojure developer"}))

; API
(defn data []
  {:name "clojure developer"})

(defn query [dataset]
  (sql/query "select * from dw.equities limit 10"))

;(defn query [dataset]
  ;(sql/query (util/multi-line-string
               ;"select *
                ;from dw.equities
                ;limit 10")))

;(defn query [dataset]
  ;(sql/query "select *
              ;from dw.:table
              ;limit 10"
             ;{:table dataset}))

;(defn query [dataset]
  ;(sql/query ["select *
               ;from dw.?
               ;limit 10"
              ;dataset]))

