(ns todo.endpoint.views.layout
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]))

(defn wrap-layout
  "Wrap the layout around a page"
  [nav content]
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible"
             :content "IE=edge"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
     [:meta {:name "description"
             :content "A zoomy web app for doing the tracking of doing todos."}]
     [:meta {:name "author"
             :content "Anonymous Candidate"}]
     [:title "TODO DO DO"]
     [:link {:rel "stylesheet" :href "/css/bootstrap.min.css"}]]
    [:body
     [:div {:class "container"}
      nav
      content]
     [:script {:src "/js/jquery-1.12.2.min.js"}]
     [:script {:src "/js/bootstrap.min.js"}]]]))
