(ns todo.endpoint.views.todos
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn done? [entity]
  (= :status/done (:todo-item/status entity)))

(defn update-status [entity]
  (if (done? entity) "todo" "done"))

(defn button-label [entity]
  (if (done? entity) "Undo" "Mark Done"))

(defn wrap-strikethrough [entity]
  (let [text (:todo-item/text entity)]
    (if (done? entity)
      [:strike text]
      text)))

(defn todo-table
  "List the todos"
  [entities]
  [:div
   [:table {:class "u-full-width"}
    [:thead
     [:tr
      [:th "Todo"]
      [:th {:colspan "2"}
       "Status"]]]
    [:tbody
     (for [e entities]
       [:form {:method "POST" :action (str "/" (:todo-item/uuid e))}
        [:input {:type "hidden"
                 :name "todo-status"
                 :id "todo-status"
                 :value (update-status e)}]
        (anti-forgery-field)
        [:tr
         [:td (wrap-strikethrough e)]
         [:td (name (:todo-item/status e))]
         [:td
          [:button {:type "submit"
                    :class "input one-half column u-pull-right"}
           (button-label e)]]]])]]
   [:form {:method "POST"}
    [:label {:for "todo-text"}
     "Enter new Todo item:"]
    [:input {:class "u-full-width" :type "text" :name "todo-text" :id "todo-text"}]
    (anti-forgery-field)]])
