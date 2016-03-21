(ns todo.endpoint.views.todos
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn todo-table
  "List the todos"
  [entities]
  [:div
   [:table {:class "u-full-width"}
    [:thead
     [:tr
      [:th "Todo"]
      [:th "Status"]]]
    [:tbody
     (for [e entities]
       [:tr
        [:td (:todo-item/text e)]
        [:td (name (:todo-item/status e))]])]]
   [:form {:method "POST"}
    [:label {:for "todo-text"}
     "Enter new Todo item:"]
    [:input {:class "u-full-width" :type "text" :name "todo-text" :id "todo-text"}]
    (anti-forgery-field)]])
