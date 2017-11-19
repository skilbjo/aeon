(ns server.middleware-policy
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import [net.sf.uadetector UserAgent UserAgentStringParser]
           [net.sf.uadetector.service UADetectorServiceFactory]))

; -- policy ----------------
(defprotocol source-value
  (to-source [this]))

(extend-protocol source-value
  clojure.lang.Keyword
  (to-source [this]
    (str "'" (name this) "'")))

(extend-protocol source-value
  java.lang.String
  (to-source [this]
    this))

(defn map-join [func separator collection]
  (apply str (interpose separator (map func collection))))

(defn to-values [collection]
  (map-join to-source " " collection))

(defprotocol sources
  (source-values [this]))

(extend-protocol sources
  java.lang.String
  (source-values [this]
    this))

(extend-protocol sources
  clojure.lang.PersistentVector
  (source-values [this]
    (to-values this)))

(extend-protocol sources
  clojure.lang.Keyword
  (source-values [this]
    (to-source this)))

(defn make-source-directive [[source-type sources]]
  (str (name source-type)
       "-src "
       (source-values sources)))

(defn get-sources [sources-map]
  (map-join make-source-directive "; " (seq sources-map)))

(defn make-directive [[directive value]]
  (str (name directive)
       " "
       (source-values value)))

(defn get-directives [directives-map]
  (map-join make-directive "; " (seq directives-map)))

(defn make-policy [policy-map]
  (let [sources    (policy-map :sources)
        directives (dissoc policy-map :sources)]
    (if (nil? sources)
      (get-directives directives)
      (str (get-sources sources)
           (when-not (empty? directives)
             (str "; " (get-directives directives)))))))

(defn load-policy
  ([]
   (edn/read-string (slurp "config/security_policy.clj")))
  ([filepath]
   (edn/read-string (-> filepath
                        io/resource
                        slurp))))

; -- browser ---------------
(defn parse-user-agent-string [user-agent-string]
  (let [safe-user-agent-string (or user-agent-string
                                   "Unknown")
        parser                 (UADetectorServiceFactory/getResourceModuleParser)
        user-agent             (.parse parser safe-user-agent-string)]
    (-> {}
        (assoc :family (str (.getFamily user-agent)))
        (assoc :name (.getName user-agent))
        (assoc :version (vec (map (fn [number-str]
                                    (try (Integer/parseInt number-str)
                                         (catch NumberFormatException e 0)))
                                  (.getGroups (.getVersionNumber user-agent))))))))

(defn get-user-agent [request]
  (let [headers    (request :headers)
        user-agent (headers "user-agent")]
    (parse-user-agent-string user-agent)))

(defn new-enough? [family version]
  (cond (= "CHROME"  family) (>= version 25)
        (= "FIREFOX" family) (>= version 23)
        (= "OPERA"   family) (>= version 15)
        (= "OPERA_MOBILE" family) (>= version 14)
        :else false))

(defn standard-header? [browser]
  (let [browser-family    (browser :family)
        browser-version (first (browser :version))]
    (and (contains? #{"CHROME" "FIREFOX" "OPERA" "OPERA_MOBILE"} browser-family)
         (new-enough? browser-family browser-version))))

(defn gecko-header? [browser]
  (let [browser-family    (browser :family)
        browser-version (first (browser :version))]
    (or (and (= "FIREFOX" browser-family)
             (<= browser-version 23))
        (and (= "IE" browser-family)
             (<= browser-version 10)))))

(defn webkit-header? [browser]
  (let [browser-family  (browser :family)
        browser-version (first (browser :version))]
    (or (and (= "CHROME" browser-family)
             (<= browser-version 25))
        (and (= "CHROME_MOBILE" browser-family)
             (<= browser-version 28))
        (and (= "BLACKBERRY_BROWSER" browser-family)
             (<= 10 browser-version))
        (and (contains? #{"SAFARI" "MOBILE_SAFARI"} browser-family)
             (>= browser-version 5)))))

(defn select-header [request]
  (let [browser (get-user-agent request)]
    (cond (standard-header? browser)   "Content-Security-Policy"
          (gecko-header?    browser)   "X-Content-Security-Policy"
          (webkit-header?   browser)   "X-Webkit-CSP"
          :else "Unknown-browser-CSP")))

; -- public ----------------
(defn add-content-security-policy
  ([handler & {:keys [config-path]}]
   (fn [request]
     (let [response (handler request)
           headers  (:headers response)]
       (assoc response :headers
              (assoc headers
                     (select-header request)
                     (make-policy (if config-path
                                    (load-policy config-path)
                                    (load-policy)))))))))

(defn wrap-referrer-policy [handler policy]
  (fn [request]
    (let [response (handler request)
          headers  (:headers response)]
      (assoc-in response
                [:headers "Referrer-Policy"]
                policy))))
