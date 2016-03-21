(ns todo.endpoint.todos-test
  (:require [clojure.test :refer :all]
            [todo.endpoint.todos :as todos]
            [todo.test-utils :as utils :refer [conn]]
            [todo.data.todos :as data]
            [ring.mock.request :as mock]
            [ring.util.http-predicates :refer [see-other? ok? success?]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(use-fixtures :each
  utils/wrap-manage-datomic)

(defn test-data []
  (let [todos [["Todo number 1" :status/done]
               ["Todo number 2" :status/todo]
               ["Todo number 3" :status/todo]]]
    (doseq [[text status] todos]
      (data/create conn text status))))

(deftest root-route-works-for-get
  (let [handler (todos/todos-endpoint {:db {:conn conn}})]
    (testing "empty table"
      (let [resp (handler (mock/request :get "/"))]
        (is (ok? resp))
        (is (re-find #"<th>Todo</th>" (:body resp)))
        (is (re-find #"<th>Status</th>" (:body resp)))))
    (testing "with some data"
      (test-data)
      (let [resp (handler (mock/request :get "/"))]
        (is (ok? resp))
        (is (re-find #"Todo number 1" (:body resp)))
        (is (re-find #"Todo number 2" (:body resp)))
        (is (re-find #"Todo number 3" (:body resp)))))))

(deftest root-route-works-for-post
  (let [handler (wrap-defaults (todos/todos-endpoint {:db {:conn conn}})
                               (assoc site-defaults :security false))]
    (testing "the happy path"
      (let [resp (handler (-> (mock/request :post "/")
                              (mock/body {"todo-text" "A new todo"})))]
        (is (see-other? resp))
        (let [resp (handler (mock/request :get (get-in resp [:headers "Location"])))]
          (is (ok? resp))
          (is (re-find #"A new todo" (:body resp))))))))
