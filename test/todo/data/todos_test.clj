(ns todo.data.todos-test
  (:require [todo.data.todos :as todos]
            [todo.component.datomic :as datomic]
            [clojure.test :refer :all]
            [datomic.api :as d]
            [com.stuartsierra.component :as component])
  (:import java.util.UUID))

(def test-config {:transactor-uri "datomic:mem://todododo-test"})

(def ^:dynamic conn nil)

(use-fixtures :each
  (fn [test]
    (let [datomic-component (-> (datomic/datomic test-config)
                                (component/start))]
      (binding [conn (:conn datomic-component)]
        (test)
        (d/delete-database (:transactor-uri test-config))))))

(deftest create-todo-works
  (testing "and returns an ID"
    (let [uuid (todos/create conn "This is a nice todo")]
      (is (= java.util.UUID (type uuid))))))

(deftest find-by-id-works
  (testing "returns nil if there's no match"
    (is (nil? (todos/find-by-id conn (d/squuid)))))
  (testing "returns an entity if there's a match"
    (let [uuid (todos/create conn "This is a crap todo" :status/done)
          entity (todos/find-by-id conn uuid)]
      (is (some? entity))
      (is (re-find #"crap todo" (:todo-item/text entity)))
      (is (= :status/done (:todo-item/status entity))))))

(deftest index-works
  (testing "returns an empty list if there's nothing there"
    (is (empty? (todos/index conn))))
  (testing "returns a list of todos otherwise"
    (let [texts ["todo 1" "todo 2" "todo 3"]
          todos (doall (map #(todos/create conn %) texts))
          index-result (todos/index conn)
          index-texts (map :todo-item/text index-result)]
      (is (= 3 (count index-result)))
      (doseq [text texts]
        (is (some #{text} index-texts))))))

(deftest update-by-id-works
  (testing "returns nil when no todo matches"
    (is (nil? (todos/update-by-id conn (d/squuid) {:todo-item/text "changed text"}))))
  (testing "returns the uuid of an updated entity"
    (let [initial-uuid (todos/create conn "New todo item")
          updated-uuid (todos/update-by-id conn
                                           initial-uuid
                                           {:todo-item/status :status/done})]
      (is (= java.util.UUID (type updated-uuid)))
      (is (= initial-uuid updated-uuid))
      (testing "updates the correct bits"
        (let [updated-entity (todos/find-by-id conn updated-uuid)]
          (is (= #{:todo-item/status :todo-item/text :todo-item/uuid} (set (keys updated-entity))))
          (is (= initial-uuid (:todo-item/uuid updated-entity)))
          (is (= "New todo item" (:todo-item/text updated-entity)))
          (is (= :status/done (:todo-item/status updated-entity))))))))

(deftest delete-by-id-works
  (testing "returns nil when no todo matches"
    (is (nil? (todos/delete-by-id conn (d/squuid)))))
  (testing "returns the uuid of a deleted entity"
    (let [initial-uuid (todos/create conn "New todo item")
          deleted-uuid (todos/delete-by-id conn initial-uuid)]
      (is (= java.util.UUID (type deleted-uuid)))
      (is (= initial-uuid deleted-uuid))
      (testing "actually retracts the entity"
        (is (nil? (todos/find-by-id conn deleted-uuid)))))))
