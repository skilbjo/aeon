(ns app.views.footer
  (:require [re-frame.core :refer [subscribe dispatch]]))

(def swagger-uri
  (str "https://skilbjo.duckdns.org" "/swagger"))

(defn footer []
  [:footer.page-footer
   [:div.container
    [:div.row]]
   [:div.footer-copyright
    [:div.container
     [:a.grey-text.text-lighten-4.left
      {:href "https://github.com/skilbjo/aeon"}
      "source code"]]
    [:div.container
     [:a.grey-text.text-lighten-4.right {:href swagger-uri} "swagger api"]]]])
