(ns todo.endpoint.todos
  (:require [compojure.core :refer :all]
            [compojure.coercions :refer [as-uuid]]
            [todo.data.todos :as todos-data]
            [todo.endpoint.views.layout :as layout]
            [todo.endpoint.views.todos :as todos-views]
            [ring.util.http-response :refer :all]))

(defn render-todos
  [conn]
  (-> (todos-data/index conn)
      (todos-views/todo-table)
      (layout/wrap-layout)
      (ok)
      (content-type "text/html")))

(defn add-todo [conn text]
  (try
    (todos-data/create conn text)
    (see-other "/")
    (catch Throwable t
      (internal-server-error! (.getMessage t)))))

(defn update-todo [conn uuid todo-status]
  (try
    (let [status (keyword (str "status/" todo-status))]
      (todos-data/update-by-id conn uuid {:todo-item/status status})
      (see-other "/"))
    (catch Throwable t
      (internal-server-error! (.getMessage t)))))

(defn delete-todo [conn uuid]
  (try
    (todos-data/delete-by-id conn uuid)
    (see-other "/")
    (catch Throwable t
      (internal-server-error! (.getMessage t)))))

(defn todos-endpoint [config]
  (let [conn (-> config :db :conn)]
    (routes
     (GET "/" []
       (render-todos conn))

     (POST "/" [todo-text]
       (add-todo conn todo-text))

     (POST "/:uuid" [uuid :<< as-uuid todo-status delete]
       (if delete
         (delete-todo conn uuid)
         (update-todo conn uuid todo-status))))))
