(ns server.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [server.util :as util]))

(def valid? s/valid?)

(s/def ::dataset string?)
(s/def ::ticker string?)
(s/def ::date string?)
(s/def ::authorization string?)

(def dataset ::dataset)
(def ticker ::ticker)
(def date ::date)

(def datasets
  #{:currency
    :economics
    :interest_rates
    :real_estate
    :equities})

(defn allowed-endpoint? [coll needle]
  (->> needle
       keyword
       (contains? coll)))
