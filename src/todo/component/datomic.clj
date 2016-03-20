(ns todo.component.datomic
  (:require [com.stuartsierra.component :as component]
            [todo.logging :refer [infof]]
            [datomic.api :as d]))

(defrecord Datomic [uri conn]
  component/Lifecycle
  (start [component]
    (when (d/create-database uri)
      (infof "Created new database: %s" uri))
    (let [conn (d/connect uri)]
      (assoc component :conn conn)))
  (stop [component]
    (dissoc component :conn)))

(defn datomic [config]
  (map->Datomic {:uri (:transactor-uri config)}))
