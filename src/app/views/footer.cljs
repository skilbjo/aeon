(ns app.views.footer
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn footer []
  [:footer.page-footer
   [:div.container
    [:div.row]]
   [:div.footer-copyright
    [:div.container
     [:a.grey-text.text-lighten-4.left
      {:href "https://github.com/skilbjo/aoin"}
      "source code"]]
    [:div.container
     [:a.grey-text.text-lighten-4.right {:href "/swagger"} "swagger api"]]]])
