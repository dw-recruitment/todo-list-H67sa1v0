(ns todo.endpoint.views.todos
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]
            [todo.endpoint.utils :as utils]
            [todo.data.todos :as todos]
            [todo.data.todo-lists :as todo-lists]
            [todo.endpoint.views.common :as common]))

(defn update-status [entity]
  (if (todos/done? entity) "todo" "done"))

(defn button-label [entity]
  (if (todos/done? entity) "Undo" "Mark Done"))

(defn status [entity]
  (when (todos/done? entity) [:span {:class "glyphicon glyphicon-ok"}]))

(defn maybe-wrap-strikethrough [entity]
  (let [text (todos/text entity)]
    (if (todos/done? entity)
      [:strike text]
      text)))

(defn new-todo-form []
  [:form {:method "POST"}
   [:div {:class "form-group"}
    [:input {:type "text"
             :class "form-control"
             :name "todo-text"
             :id "todo-text"
             :placeholder "Enter new todo item"}]]
   (anti-forgery-field)])

(defn todo-table
  "List the todos"
  [t-list entities]
  [:div {:class "row"}
   [:div {:class "col-md-8"}
    [:h3 (todo-lists/title t-list)]
    [:table {:class "table-striped table"}
     [:thead
      [:tr
       [:th "Todo"]
       [:th "Status"]
       [:th "Actions"]]]
     [:tbody
      (for [e entities]
        [:form {:method "POST" :action (utils/todo-path t-list e)}
         [:input {:type "hidden"
                  :name "todo-status"
                  :id "todo-status"
                  :value (update-status e)}]
         (anti-forgery-field)
         [:tr
          [:td (maybe-wrap-strikethrough e)]
          [:td (status e)]
          [:td
           [:button {:type "submit"
                     :class "btn btn-success col-md-5"}
            (button-label e)]
           [:button {:type "submit"
                     :name "delete"
                     :value "true"
                     :class "btn btn-danger pull-right col-md-5"}
            "Delete"]]]])]]
    (new-todo-form)]])
