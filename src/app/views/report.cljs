(ns app.views.report
  (:require [app.views.util :as views-util]
            [clojure.string :as string]
            [reagent.core :as reagent]
            [re-frame-datatable.core :as dt]
            [app.subs :as subs]
            [app.util :as util]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- table -------------------------------------------------------------------
(defn ^:private table [report sort-key]
  [dt/datatable
   (keyword report)
   [(keyword report)]
   [{::dt/column-key   [sort-key]
     ::dt/column-label "Description"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:market_value]
     ::dt/column-label "Market Value $"
     ::dt/render-fn    (fn [v]        ;; not sure if needed
                         (try (int v) ;; for numerical sort
                              (catch js/Object e v)))
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
                        ::dt/per-page 40}
    ::dt/table-classes ["ui" "celled" "stripped" "table"]}])

(defn ^:private portfolio-table []
  (table "portfolio" :description))

(defn ^:private asset-type-table []
  (table "asset-type" :asset_type))

(defn ^:private capitalization-table []
  (table "capitalization" :capitalization))

(defn ^:private investment-style-table []
  (table "investment-style" :investment-style))

(defn ^:private location-table []
  (table "location" :location))

(defn ^:private display-report [report table-fn]
  (let [loading        @(subscribe [:loading])
        report-data    @(subscribe [(-> report keyword)])
        now            util/now]
    [:div.container
     [:div.row
      [:h3 (-> report string/capitalize (str " report for " now))]
      (when report-data
        [table-fn])]]))

(defn portfolio []
  (display-report "portfolio" portfolio-table))

(defn asset-type []
  (display-report "asset-type" asset-type-table))

(defn capitalization []
  (display-report "capitalization" capitalization-table))

(defn investment-style []
  (display-report "investment-style" investment-style-table))

(defn location []
  (display-report "location" location-table))
