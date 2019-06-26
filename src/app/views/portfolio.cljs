(ns app.views.portfolio
  (:require [app.views.util :as views-util]
            [reagent.core :as reagent]
            [re-frame-datatable.core :as dt]
            [app.subs :as subs]
            [app.util :as util]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- portfolio ---------------------------------------------------------------
(defn portfolio-table []
  (print "portfolio-table called")
  [dt/datatable
   :portfolio
   [:portfolio]
   [{::dt/column-key   [:ticker]
     ::dt/column-label "Ticker"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:mix_%]
     ::dt/column-label "Mix %"
     ::dt/sorting      {::dt/enabled? true}}
    {::dt/column-key   [:description]
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

(defn portfolio []
  (let [loading        @(subscribe [:loading])
        portfolio      @(subscribe [:portfolio])
        now            util/now]
    [:div.container
     [:div.row
      [:h3 (str "Portfolio for " now)]
      (when portfolio
        [portfolio-table])]]))
