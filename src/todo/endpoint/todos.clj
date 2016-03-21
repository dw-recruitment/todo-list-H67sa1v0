(ns todo.endpoint.todos
  (:require [compojure.core :refer :all]
            [todo.data.todos :as todos-data]
            [todo.endpoint.views.layout :as layout]
            [todo.endpoint.views.todos :as todos-views]))

(defn render-todos
  [conn]
  (-> (todos-data/index conn)
      (todos-views/todo-table)
      (layout/wrap-layout)))

(defn todos-endpoint [config]
  (let [conn (-> config :db :conn)]
    (routes
     (GET "/" [] (render-todos conn)))))
