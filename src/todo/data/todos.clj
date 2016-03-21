(ns todo.data.todos
  (:require [datomic.api :as d]
            [todo.logging :refer [error errorf]])
  (:import java.util.concurrent.ExecutionException))

(defn create
  "Make a new todo item."
  ([conn text]
   (create conn text :status/todo))
  ([conn text status]
   {:pre [(contains? #{:status/todo :status/done} status)]}
   (try
     (let [squuid (d/squuid)]
       (deref (d/transact conn
                          `[{:db/id #db/id[:todos]
                             :todo-item/uuid ~squuid
                             :todo-item/text ~text
                             :todo-item/status ~status}]))
       squuid)
     (catch java.util.concurrent.ExecutionException e
       (error e "Couldn't create todo")))))

(defn find-by-id
  "Find a todo with a specified ID"
  [conn uuid]
  (when-let [entity-id (d/q '[:find ?t . :in $ ?u :where [?t :todo-item/uuid ?u]]
                            (d/db conn)
                            uuid)]
    (d/entity (d/db conn) entity-id)))

(defn index
  "List all the todos"
  [conn]
  (let [results (d/q '[:find [?t ...] :where [?t :todo-item/uuid]] (d/db conn))]
    (map (partial d/entity (d/db conn)) results)))

(defn update-by-id
  "Update a todo"
  [conn uuid m]
  (when-let [entity (find-by-id conn uuid)]
    (try
      (let [new-entity (merge (select-keys entity (keys entity))
                              m
                              {:db/id (:db/id entity)})]
        (deref (d/transact conn [new-entity]))
        uuid)
      (catch java.util.concurrent.ExecutionException e
        (errorf e "Couldn't update todo: %s" uuid)))))

(defn delete-by-id
  "Delete a todo"
  [conn uuid]
  (when-let [entity (find-by-id conn uuid)]
    (try
      (deref (d/transact conn [[:db.fn/retractEntity (:db/id entity)]]))
      uuid
      (catch java.util.concurrent.ExecutionException e
        (errorf e "Couldn't delete todo: %s" uuid)))))
