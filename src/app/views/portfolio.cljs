(ns app.views.portfolio
  (:require [app.views.util :as util]
            [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- portfolio ---------------------------------------------------------------
(defn portfolio []
  (let [portfolio        @(subscribe [:portfolio])]
   [:div.container
    [:div.row
     [:p "Portfolio is"]
     [:p portfolio]
     #_(when portfolio)
      [:p portfolio]]]))
