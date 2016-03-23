(ns seed
  (:require [datomic.api :as d]))

(def seed-data
  `[{:db/id #db/id[:todos -100]
     :todo-list/uuid ~(d/squuid)
     :todo-list/title "Work todo list"}
    {:db/id #db/id[:todos -200]
     :todo-list/uuid ~(d/squuid)
     :todo-list/title "Home todo list"}

    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Make some lunch"
     :todo-list/_todo-items #db/id[:todos -100]}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/done
     :todo-item/text "Eat some lunch"
     :todo-list/_todo-items #db/id[:todos -100]}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Buy a car"
     :todo-list/_todo-items #db/id[:todos -100]}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/done
     :todo-item/text "Get a job"
     :todo-list/_todo-items #db/id[:todos -200]}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Write a novel"
     :todo-list/_todo-items #db/id[:todos -200]}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/todo
     :todo-item/text "Take a bath"
     :todo-list/_todo-items #db/id[:todos -200]}
    {:db/id #db/id[:todos]
     :todo-item/uuid ~(d/squuid)
     :todo-item/status :status/done
     :todo-item/text "Fly to Tokyo"
     :todo-list/_todo-items #db/id[:todos -200]}])

(defn install-seed-data
  [conn]
  (d/transact conn seed-data))
