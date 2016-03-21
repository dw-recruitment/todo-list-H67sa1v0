(ns seed
  (:require [datomic.api :as d]))

(def seed-data
  `[{:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Make some lunch"}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/done
     :todo-item/text "Eat some lunch"}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Buy a car"}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/done
     :todo-item/text "Get a job"}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Write a novel"}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Take a bath"}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/done
     :todo-item/text "Fly to Tokyo"}])

(defn install-seed-data
  [conn]
  (d/transact conn seed-data))
