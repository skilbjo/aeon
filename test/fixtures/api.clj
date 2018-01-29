(ns fixtures.api
  (:require [clojure.data.json :as json]))

(def api-response-from-currency
  "{\"body\": [{
   \"dataset\":  \"CURRFX\",
   \"ticker\":   \"GBPUSD\",
   \"currency\": \"GBP\",
   \"date\":     \"2018-01-25T08:00:00Z\",
   \"rate\":     1.4150274991989,
   \"high_est\": 1.4281226396561,
   \"low_est\":  1.4147073030472
   }]}")

;(def to-fix
  ;{:body ({:currency "GBP",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-15T08:00:00.000-00:00",
           ;:high_est 1.34498989582060M,
           ;:low_est 1.33081364631650M,
           ;:rate 1.34329152107240M,
           ;:ticker "GBPUSD"}
          ;{:currency "EUR",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-15T08:00:00.000-00:00",
           ;:high_est 1.18140053749080M,
           ;:low_est 1.17616617679600M,
           ;:rate 1.17749571800230M,
           ;:ticker "EURUSD"}
          ;{:currency "GBP",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-14T08:00:00.000-00:00",
           ;:high_est 1.34556913375850M,
           ;:low_est 1.33951294422150M,
           ;:rate 1.34096789360050M,
           ;:ticker "GBPUSD"}
          ;{:currency "EUR",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-14T08:00:00.000-00:00",
           ;:high_est 1.18598639965060M,
           ;:low_est 1.17716300487520M,
           ;:rate 1.18358600139620M,
           ;:ticker "EURUSD"}
          ;{:currency "EUR",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-13T08:00:00.000-00:00",
           ;:high_est 1.17702448368070M,
           ;:low_est 1.17304801940920M,
           ;:rate 1.17426025867460M,
           ;:ticker "EURUSD"}
          ;{:currency "GBP",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-13T08:00:00.000-00:00",
           ;:high_est 1.33763158321380M,
           ;:low_est 1.33136284351350M,
           ;:rate 1.33194816112520M,
           ;:ticker "GBPUSD"}
          ;{:currency "EUR",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-12T08:00:00.000-00:00",
           ;:high_est 1.17929947376250M,
           ;:low_est 1.17189335823060M,
           ;:rate 1.17775917053220M,
           ;:ticker "EURUSD"}
          ;{:currency "GBP",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-12T08:00:00.000-00:00",
           ;:high_est 1.33773899078370M,
           ;:low_est 1.33129203319550M,
           ;:rate 1.33493518829350M,
           ;:ticker "GBPUSD"}
          ;{:currency "GBP",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-11T08:00:00.000-00:00",
           ;:high_est 1.34264242649080M,
           ;:low_est 1.33500659465790M,
           ;:rate 1.33920800685880M,
           ;:ticker "GBPUSD"}
          ;{:currency "EUR",
           ;:dataset "CURRFX",
           ;:date #inst "2017-12-11T08:00:00.000-00:00",
           ;:high_est 1.18104183673860M,
           ;:low_est 1.17670595645900M,
           ;:rate 1.17699682712550M,
           ;:ticker "EURUSD"})}
  ;)

(def result
  (-> api-response-from-currency
      (json/read-str :key-fn keyword)))
