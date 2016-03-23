(ns todo.endpoint.utils)

(defn todo-path [t-list todo]
  (str "/lists/" (:todo-list/uuid t-list) "/" (:todo-item/uuid todo)))

(defn todo-list-path [todo-list]
  (str "/lists/" (:todo-list/uuid todo-list)))
