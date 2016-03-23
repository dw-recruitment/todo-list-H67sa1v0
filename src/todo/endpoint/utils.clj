(ns todo.endpoint.utils
  (:require [todo.data.todo-lists :as todo-lists]
            [todo.data.todos :as todos]))

(defn todo-path [t-list todo]
  (str "/lists/" (todo-lists/uuid t-list) "/todos/" (todos/uuid todo)))

(defn todo-list-path [todo-list]
  (str "/lists/" (todo-lists/uuid todo-list)))

(defn new-list-path []
  (str "/lists/new"))

(defn edit-list-path [todo-list]
  (str "/lists/" (todo-lists/uuid todo-list) "/edit"))

(defn create-todo-path [t-list]
  (str "/lists/" (todo-lists/uuid t-list) "/todos"))

(defn create-todo-list-path []
  "/lists")
