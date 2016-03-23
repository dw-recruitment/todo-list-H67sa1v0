(ns todo.data.todo-lists-test
  (:require [todo.data.todo-lists :as todo-lists]
            [clojure.test :refer :all]
            [datomic.api :as d]
            [todo.test-utils :as utils :refer [conn]])
  (:import java.util.UUID))

(use-fixtures :each
  utils/wrap-manage-datomic)

(deftest create-todo-list-works
  (testing "and returns an ID"
    (let [uuid (todo-lists/create conn "This is a nice todo list")]
      (is (= java.util.UUID (type uuid))))))

(deftest find-by-id-works
  (testing "returns nil if there's no match"
    (is (nil? (todo-lists/find-by-id conn (d/squuid)))))
  (testing "returns an entity if there's a match"
    (let [uuid (todo-lists/create conn "This is a terrible list")
          entity (todo-lists/find-by-id conn uuid)]
      (is (some? entity))
      (is (re-find #"terrible list" (:todo-list/title entity))))))

(deftest todo-lists-index-works
  (testing "returns an empty list if there are none"
    (is (empty? (todo-lists/index conn))))
  (testing "returns a list of todos otherwise"
    (let [titles ["List 1" "List 2" "List 3"]
          _ (doseq [title titles] (todo-lists/create conn title))
          index-result (todo-lists/index conn)
          index-titles (map :todo-list/title index-result)]
      (is (= 3 (count index-result)))
      (doseq [title titles]
        (is (some #{title} index-titles))))))
