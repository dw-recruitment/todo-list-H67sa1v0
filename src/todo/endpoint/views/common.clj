(ns todo.endpoint.views.common
  (:require [todo.endpoint.utils :as utils]
            [todo.data.todo-lists :as todo-lists]))

(defn nav [lists]
  [:nav {:class "navbar navbar-default col-md-8"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand"
          :href "/"}
      "TODO DO DO"]]
    [:ul {:class "nav navbar-nav navbar-right"}
     [:li {:class "dropdown"}
      [:a {:href "#"
           :class "dropdown-toggle"
           :data-toggle "dropdown"
           :role "button"
           :aria-haspopup "true"
           :aria-expanded "false"}
       "Lists"
       [:span {:class "caret"}]]
      [:ul {:class "dropdown-menu"}
       (for [list lists]
         [:li
          [:a {:href (utils/todo-list-path list)}
           (todo-lists/title list)]])]]]]])
