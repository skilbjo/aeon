(ns app.views.footer
  (:require [re-frame.core :refer [subscribe dispatch]]))

(def source-code
  "https://github.com/skilbjo/aeon")

(def swagger-uri
  (str "https://skilbjo.duckdns.org" "/swagger"))

(def statuspage
  "https://skilbjo.statuspage.io/")

(defn footer []
  [:footer.page-footer
   [:div.container
    [:div.row]]
   [:div.footer-copyright
    [:div.container
     [:a.grey-text.text-lighten-4.left
      {:href source-code} "source code"]
     [:div.container
      [:div.container
       [:a.grey-text.text-lighten-4.left
        {:href statuspage} "status"]]
      [:div.container
       [:div.container
        [:a.grey-text.text-lighten-4.left
         {:href swagger-uri} "swagger api"]]]]]]])
