(ns todo.endpoint.views.todo-lists
  (:require [todo.endpoint.utils :as utils]
            [todo.data.todo-lists :as todo-lists]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn form
  "Content for a page to enter a new todo-list title"
  [t-list]
  [:div {:class "row"}
   [:div {:class "col-md-8"}
    [:h3 (if t-list "Edit todo list" "New todo list")]
    [:form {:method "POST"
            :action (if t-list
                      (utils/todo-list-path t-list)
                      (utils/create-todo-list-path))}
     [:div {:class "form-group"}
      [:input (merge {:type "text"
                      :class "form-control"
                      :name "title"
                      :id "title"
                      :placeholder "Name your todo list"}
                     (when t-list
                       {:value (todo-lists/title t-list)}))]
      (anti-forgery-field)]]]])
