(ns todo.endpoint.todo-lists
  (:require [compojure.core :refer :all]
            [compojure.coercions :refer [as-uuid]]
            [todo.data.todo-lists :as todo-lists-data]
            [todo.data.todos :as todos-data]
            [todo.endpoint.views.layout :as layout]
            [todo.endpoint.views.todos :as todos-views]
            [todo.endpoint.views.todo-lists :as todo-lists-views]
            [todo.endpoint.views.common :as common]
            [todo.endpoint.utils :as utils]
            [ring.util.http-response :refer [see-other
                                             ok
                                             content-type
                                             internal-server-error!]]))

(defn redirect-to-list
  [conn list-uuid]
  (->> list-uuid
       (todo-lists-data/find-by-id conn)
       (utils/todo-list-path)
       (see-other)))

(defn redirect-to-default-list
  [conn]
  (if-let [default-list (first (todo-lists-data/index conn))]
    (see-other (utils/todo-list-path default-list))
    (see-other (utils/new-list-path))))

(defn render-todos
  [conn list-uuid]
  (let [table (todos-views/todo-table (todo-lists-data/find-by-id conn list-uuid)
                                      (todos-data/index conn list-uuid))
        nav (common/nav (todo-lists-data/index conn))]
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

(defn render-todo-list-form
  ([conn]
   (render-todo-list-form conn nil))
  ([conn list-uuid]
   (let [nav (common/nav (todo-lists-data/index conn))
         form (-> (when list-uuid
                    (todo-lists-data/find-by-id conn list-uuid))
                  (todo-lists-views/form))]
     (-> (layout/wrap-layout nav form)
         (ok)
         (content-type "text/html")))))

(defn add-todo-list [conn title]
  (with-handle-error
    (let [uuid (todo-lists-data/create conn title)]
      (redirect-to-list conn uuid))))

(defn update-todo-list [conn list-uuid title]
  (with-handle-error
    (todo-lists-data/update-by-id conn list-uuid title)
    (redirect-to-list conn list-uuid)))

(defn delete-todo-list [conn list-uuid]
  (with-handle-error
    (todo-lists-data/delete-by-id conn list-uuid)
    (redirect-to-default-list conn)))

(defn todo-lists-endpoint [config]
  (let [conn (-> config :db :conn)]
    (routes
     (GET "/" []
       (redirect-to-default-list conn))

     (context "/lists" []
       (GET "/new" []
         (render-todo-list-form conn))

       (POST "/" [title]
         (add-todo-list conn title))

       (context "/:list-uuid" [list-uuid :<< as-uuid]
         (GET "/" []
           (render-todos conn list-uuid))

         (GET "/edit" []
           (render-todo-list-form conn list-uuid))

         (POST "/" [title delete]
           (if delete
             (delete-todo-list conn list-uuid)
             (update-todo-list conn list-uuid title)))

         (context "/todos" []
           (GET "/" []
             (render-todos conn list-uuid))

           (POST "/" [todo-text]
             (add-todo conn list-uuid todo-text))

           (POST "/:todo-uuid" [todo-uuid :<< as-uuid todo-status delete]
             (if delete
               (delete-todo conn list-uuid todo-uuid)
               (update-todo conn list-uuid todo-uuid todo-status)))))))))
