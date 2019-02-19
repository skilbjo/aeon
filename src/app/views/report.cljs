(ns app.views.report
  (:require [app.views.util :as views-util]
            [clojure.string :as string]
            [reagent.core :as reagent]
            [re-frame-datatable.core :as dt]
            [app.subs :as subs]
            [app.util :as util]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- portfolio ---------------------------------------------------------------
(defn table []
  [dt/datatable
   :portfolio
   [:portfolio]
   [{::dt/column-key   [:description]
     ::dt/column-label "Description"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:market_value]
     ::dt/column-label "Market Value $"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:today_gain_loss]
     ::dt/column-label "Today Gain/Loss $"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:today_gain_loss_%]
     ::dt/column-label "Today Gain/Loss %"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:ytd_gain_loss]
     ::dt/column-label "YTD Gain/Loss $"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:ytd_gain_loss_%]
     ::dt/column-label "YTD Gain/Loss %"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:total_gain_loss]
     ::dt/column-label "Total Gain/Loss $"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:total_gain_loss_%]
     ::dt/column-label "Total Gain/Loss %"
     ::dt/sorting      {::dt/enabled? true}}]
   {::dt/pagination    {::dt/enabled? true
                        ::dt/per-page 30}
    ::dt/table-classes ["ui" "celled" "stripped" "table"]}])

(defn display-report [report]
  (let [loading        @(subscribe [:loading])
        report-data    @(subscribe [(-> report keyword)])
        now            util/now]
    [:div.container
     [:div.row
      [:h3 (-> report string/capitalize (str " report for " now))]
      (when report-data
        [table])]]))

(defn portfolio []
  (display-report "portfolio"))

(defn asset-type []
  (display-report "asset-type"))

(defn capitalization []
  (display-report "capitalization"))

(defn investment-style []
  (display-report "investment-style"))

(defn location []
  (display-report "location"))
