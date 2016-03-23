(ns todo.endpoint.todo-lists
  (:require [compojure.core :refer :all]
            [compojure.coercions :refer [as-uuid]]
            [todo.data.todo-lists :as todo-lists-data]
            [todo.data.todos :as todos-data]
            [todo.endpoint.views.layout :as layout]
            [todo.endpoint.views.todos :as todos-views]
            [ring.util.http-response :refer [see-other
                                             ok
                                             content-type
                                             internal-server-error!]]))

(defn todo-list-path [todo-list]
  (str "/lists/" (:todo-list/uuid todo-list)))

(defn redirect-to-list
  [conn list-uuid]
  (->> list-uuid
       (todo-lists-data/find-by-id conn)
       (todo-list-path)
       (see-other)))

(defn redirect-to-default-list
  [conn]
  (let [default-list (first (todo-lists-data/index conn))]
    (see-other (todo-list-path default-list))))

(defn render-todos
  [conn list-uuid]
  (let [table (todos-views/todo-table (todo-lists-data/find-by-id conn list-uuid)
                                      (todos-data/index conn list-uuid))
        nav (todos-views/nav (todo-lists-data/index conn))]
    (-> (layout/wrap-layout nav table)
        (ok)
        (content-type "text/html"))))

(defmacro with-handle-error
  [& forms]
  `(try
     ~@forms
     (catch Throwable t#
       (internal-server-error! (.getMessage t#)))))

(defn add-todo [conn list-uuid text]
  (with-handle-error
    (let [t-list (todo-lists-data/find-by-id conn list-uuid)]
      (todos-data/create conn t-list text)
      (redirect-to-list conn list-uuid))))

(defn update-todo [conn list-uuid uuid todo-status]
  (with-handle-error
    (let [status (keyword (str "status/" todo-status))]
      (todos-data/update-by-id conn uuid {:todo-item/status status})
      (redirect-to-list conn list-uuid))))

(defn delete-todo [conn list-uuid uuid]
  (with-handle-error
    (todos-data/delete-by-id conn uuid)
    (redirect-to-list conn list-uuid)))

(defn todo-lists-endpoint [config]
  (let [conn (-> config :db :conn)]
    (routes
     (GET "/" []
       (redirect-to-default-list conn))

     (context "/lists" []
       (context "/:list-uuid" [list-uuid :<< as-uuid]
         (GET "/" []
           (render-todos conn list-uuid))

         (POST "/" [todo-text]
           (add-todo conn list-uuid todo-text))

         (POST "/:todo-uuid" [todo-uuid :<< as-uuid todo-status delete]
           (if delete
             (delete-todo conn list-uuid todo-uuid)
             (update-todo conn list-uuid todo-uuid todo-status))))))))
