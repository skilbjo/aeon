(ns app.views.util)

;; -- util --------------------------------------------------------------------
(defn errors-list
  [errors]
  [:ul.error-messages
   (for [[key [val]] errors]
     ^{:key key} [:li (str (name key) " " val)])])

(defn error
  [error]
  [:ul.error-messages {:data-error "wrong"}
   [:li (str error)]])
