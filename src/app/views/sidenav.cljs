(ns app.views.sidenav)

;; this is a wip... when added to views, only get hamburger menu, no action
;; when hamburger is pressed ... this may have something to do with the javascript
;; in materialize css not registering with the clojurescript

(defn sidenav []
  [:ul.sidenav.sidenav-fixed {:id "slide-out"}
   [:li
    [:a {:href "#!"} "hello"]]]
  [:a {:href "#" :data-target "slide-out" :class "sidenav-trigger"}
   [:i.material-icons "menu"]])
