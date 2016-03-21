(ns todo.endpoint.views.todos)

(defn todo-table
  "List the todos"
  [entities]
  [:table {:class "u-full-width"}
   [:thead
    [:tr
     [:th "Todo"]
     [:th "Status"]]]
   [:tbody
    (for [e entities]
      [:tr
       [:td (:todo-item/text e)]
       [:td (name (:todo-item/status e))]])]])
