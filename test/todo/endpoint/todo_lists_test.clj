(ns todo.endpoint.todo-lists-test
  (:require [clojure.test :refer :all]
            [todo.endpoint.todo-lists :as todo-lists]
            [todo.endpoint.utils :refer [todo-path todo-list-path create-todo-path
                                         create-todo-list-path]]
            [todo.test-utils :as utils :refer [conn]]
            [todo.data.todos :as todos-data]
            [todo.data.todo-lists :as todo-lists-data]
            [ring.mock.request :as mock]
            [ring.util.http-predicates :refer [see-other? ok? success?]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(use-fixtures :each
  utils/wrap-manage-datomic)

(defn handler [conn]
  (wrap-defaults (todo-lists/todo-lists-endpoint {:db {:conn conn}})
                 (assoc site-defaults :security false)))

(defn location [resp]
  (get-in resp [:headers "Location"]))

(defn test-data [conn]
  (let [todos [["Todo number 1" :status/done]
               ["Todo number 2" :status/todo]
               ["Todo number 3" :status/todo]]
        todo-lists ["The Todo List"
                    "The Other Todo List"]]
    (doseq [title todo-lists]
      (todo-lists-data/create conn title))
    (let [t-list (first (todo-lists-data/index conn))]
      (doseq [[text status] todos]
        (todos-data/create conn t-list text status)))))

(deftest root-route-works-for-get
  (let [handler (handler conn)]
    (test-data conn)
    (testing "redirects to default (first) list url"
      (let [resp (handler (mock/request :get "/"))
            t-list (first (todo-lists-data/index conn))]
        (is (see-other? resp))
        (let [location (location resp)]
          (is (re-find (re-pattern (str (:todo-list/uuid t-list))) location))
          (testing "and takes us to the default list"
            (let [resp (handler (mock/request :get location))]
              (is (ok? resp))
              (is (re-find #"<th>Todo</th>" (:body resp)))
              (is (re-find #"<th.*>Status</th>" (:body resp)))
              (is (re-find #"Todo number 1" (:body resp)))
              (is (re-find #"Todo number 2" (:body resp)))
              (is (re-find #"Todo number 3" (:body resp))))))))))

(deftest create-todo-endpoint-works
  (let [handler (handler conn)]
    (test-data conn)
    (testing "the happy path"
      (let [t-list (first (todo-lists-data/index conn))
            resp (handler (-> (mock/request :post (create-todo-path t-list))
                              (mock/body {"todo-text" "A new todo"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (location resp)))]
          (is (ok? resp))
          (is (re-find #"A new todo" (:body resp))))))))

(deftest update-todo-endpoint-works
  (let [handler (handler conn)]
    (test-data conn)
    (testing "the happy path"
      (let [t-list (first (todo-lists-data/index conn))
            todo (->> (todos-data/create conn t-list "This is a great todo")
                      (todos-data/find-by-id conn))
            resp (handler (-> (mock/request :post (todo-path t-list todo))
                              (mock/body {"todo-status" "done"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (location resp)))]
          (is (ok? resp))
          (is (re-find #"is a great todo" (:body resp)))
          (is (re-find #"glyphicon-ok" (:body resp))))))))

(deftest delete-todo-endpoint-works
  (let [handler (handler conn)]
    (test-data conn)
    (testing "the happy path"
      (let [t-list (first (todo-lists-data/index conn))
            todo (->> (todos-data/create conn t-list "This is a great todo")
                      (todos-data/find-by-id conn))
            resp (handler (-> (mock/request :post (todo-path t-list todo))
                              (mock/body {"delete" "true"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (location resp)))]
          (is (ok? resp))
          (is (not (re-find #"is a great todo" (:body resp)))))))))

(deftest create-todo-list-endpoint-works
  (let [handler (handler conn)]
    (testing "the happy path"
      (let [resp (handler (-> (mock/request :post (create-todo-list-path))
                              (mock/body {"title" "A new hope"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (location resp)))]
          (is (ok? resp))
          (is (re-find #"A new hope" (:body resp))))))))

(deftest update-todo-list-endpoint-works
  (let [handler (handler conn)]
    (testing "the happy path"
      (let [t-list (->> (todo-lists-data/create conn "A new hope")
                        (todo-lists-data/find-by-id conn))
            resp (handler (-> (mock/request :post (todo-list-path t-list))
                              (mock/body {"title" "an old friend"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (location resp)))]
          (is (ok? resp))
          (is (re-find #"an old friend" (:body resp))))))))

(deftest delete-todo-list-endpoint-works
  (let [handler (handler conn)]
    (test-data conn)
    (testing "the happy path"
      (let [t-list (->> (todo-lists-data/create conn "A new hope")
                        (todo-lists-data/find-by-id conn))
            resp (handler (-> (mock/request :post (todo-list-path t-list))
                              (mock/body {"delete" "true"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (location #spy/p resp)))]
          (is (ok? resp))
          (is (not (re-find #"A new hope" (:body resp)))))))))
