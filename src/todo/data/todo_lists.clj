(ns todo.data.todo-lists
  (:require [datomic.api :as d]
            [todo.logging :refer [error errorf]])
  (:import java.util.concurrent.ExecutionException))

(defn title [t-list]
  (:todo-list/title t-list))

(defn uuid [t-list]
  (:todo-list/uuid t-list))

(defn index
  "List all the todo lists"
  [conn]
  (let [results (d/q '[:find [?t ...] :where [?t :todo-list/uuid]] (d/db conn))]
    (->> results
         (map (partial d/entity (d/db conn)))
         (sort-by :todo-list/uuid))))

(defn find-by-id
  "Find a list by UUID"
  [conn uuid]
  (when-let [entity-id (d/q '[:find ?t . :in $ ?u :where [?t :todo-list/uuid ?u]]
                            (d/db conn)
                            uuid)]
    (d/entity (d/db conn) entity-id)))

(defn create
  "Create an empty todo list"
  [conn title]
  (try
    (let [squuid (d/squuid)]
      (deref (d/transact conn
                         `[{:db/id #db/id[:todos]
                            :todo-list/uuid ~squuid
                            :todo-list/title ~title}]))
      squuid)
    (catch java.util.concurrent.ExecutionException e
      (error e "Couldn't create todo list")
      (throw e))))

(defn update-by-id
  "Update a todo list"
  [conn uuid title]
  (try
    (when-let [entity (find-by-id conn uuid)]
      (deref (d/transact conn [{:db/id (:db/id entity)
                                :todo-list/title title}]))
      uuid)
    (catch java.util.concurrent.ExecutionException e
      (errorf e "Couldn't update todo list: %s" uuid)
      (throw e))))

(defn delete-by-id
  "Delete a todo list. This deletes all associated todo items."
  [conn uuid]
  (try
    (when-let [entity (find-by-id conn uuid)]
      (deref (d/transact conn [[:db.fn/retractEntity (:db/id entity)]]))
      uuid)
    (catch java.util.concurrent.ExecutionException e
      (errorf e "Couldn't delete todo list: %s" uuid)
      (throw e))))
