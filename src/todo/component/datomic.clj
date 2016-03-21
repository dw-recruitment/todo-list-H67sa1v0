(ns todo.component.datomic
  (:require [com.stuartsierra.component :as component]
            [todo.logging :refer [infof]]
            [datomic.api :as d]
            [io.rkn.conformity :as c]))

(defn bootstrap-database
  "Bootstrap schema into the database."
  [uri]
  (when (d/create-database uri)
    (infof "Created new database: %s" uri))
  (let [conn (d/connect uri)
        norms (c/read-resource "todo/schema/todo.edn")]
    (doseq [fresh-norm (c/ensure-conforms conn norms)]
      (infof "Applied new norm: %s" fresh-norm))
    conn))

(defrecord Datomic [uri conn]
  component/Lifecycle
  (start [component]
    (let [conn (bootstrap-database uri)]
      (assoc component :conn conn)))
  (stop [component]
    (dissoc component :conn)))

(defn datomic [config]
  (map->Datomic {:uri (:transactor-uri config)}))
