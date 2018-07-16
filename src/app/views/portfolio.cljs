(ns app.views.portfolio
  (:require [app.views.util :as util]
            [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

;; -- portfolio ---------------------------------------------------------------
(defn portfolio []
  [:div.container
   [:div.row
    [:p "the portfolio awaits!"]]])
