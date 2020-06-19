(ns app.views.footer
  (:require [app.events :as events :refer [backend-uri]]
            [re-frame.core :refer [subscribe dispatch]]))

(def swagger-uri
  (str events/backend-uri "/swagger"))

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
