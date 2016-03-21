(ns todo.endpoint.views.layout
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]))

(defn wrap-layout
  "Wrap the layout around a page"
  [content]
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:title "TODO DO DO"]
     [:meta {:name "description"
             :content "A zoomy web app for doing the tracking of doing todos."}]
     [:meta {:name "author" :content "Anonymous Candidate"}]

     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]

     [:link {:href "//fonts.googleapis.com/css?family=Raleway:400,300,600"
             :rel "stylesheet"
             :type "text/css"}]

     [:link {:rel "stylesheet" :href "css/normalize.css"}]
     [:link {:rel "stylesheet" :href "css/skeleton.css"}]

     [:link {:rel "icon" :type "image/png" :href "images/favicon.png"}]]

    [:body
     [:div {:class "container"}
      content]]]))
