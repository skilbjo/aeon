(ns app.spec
  (:require [cljs.spec.alpha :as s]
            #_[re-frame.core :as rf]))

(def valid? s/valid?)
(def explain-str s/explain-str)

;; spec
(s/def ::id int?)
(s/def ::text string?)
(s/def ::stuff (s/keys :req-un [::id ::text]))
(s/def ::stuffs (s/and
                 (s/map-of ::id ::stuff)
                 #(instance? PersistentTreeMap %)))
(s/def ::db (s/keys :req-un [::stuffs]))

(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db))
                    {}))))

#_(def check-spec-interceptor (rf/after (partial check-and-throw)))
