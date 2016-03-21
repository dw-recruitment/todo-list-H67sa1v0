(ns todo.component.datomic-test
  (:require [clojure.test :refer :all]
            [todo.component.datomic :refer :all]
            [datomic.api :as d]
            [com.stuartsierra.component :as component]))

(def test-config {:transactor-uri "datomic:mem://todododo-test"})

(use-fixtures :each
  (fn [test]
    (test)
    (d/delete-database (:transactor-uri test-config))))

(deftest datomic-component-works
  (testing "starts ok"
    (let [component (datomic test-config)]
      (is (nil? (:conn component)))
      (is (= (:transactor-uri test-config) (:uri component)))
      (let [running-component (component/start component)]
        (is (some? (:conn running-component)))
        (component/stop component))))
  (testing "inserts and retrieves data"
    (let [component (-> (datomic test-config)
                        (component/start))
          conn (:conn component)
          res1 (d/transact conn [{:db/id #db/id[:db.part/db]
                                  :db/ident :person/name
                                  :db/valueType :db.type/string
                                  :db/cardinality :db.cardinality/one
                                  :db.install/_attribute :db.part/db}])
          res2 (d/transact conn [{:db/id #db/id[:db.part/user]
                                  :person/name "Jonathan"}])]
      (is (some? (:tx-data @res1)))
      (is (some? (:tx-data @res2)))
      (let [res (d/q '[:find [?person ...]
                       :in $ ?name
                       :where [?person :person/name ?name]]
                     (d/db conn)
                     "Jonathan")]
        (is (some? res))
        (println res))
      (component/stop component))))
