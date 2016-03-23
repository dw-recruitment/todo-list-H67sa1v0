(ns todo.endpoint.views.todos
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn done? [entity]
  (= :status/done (:todo-item/status entity)))

(defn update-status [entity]
  (if (done? entity) "todo" "done"))

(defn button-label [entity]
  (if (done? entity) "Undo" "Mark Done"))

(defn status [entity]
  (when (done? entity) [:span {:class "glyphicon glyphicon-ok"}]))

(defn maybe-wrap-strikethrough [entity]
  (let [text (:todo-item/text entity)]
    (if (done? entity)
      [:strike text]
      text)))

(defn todo-path [t-list todo]
  (str "/lists/" (:todo-list/uuid t-list) "/" (:todo-item/uuid todo)))

(defn new-todo-form
  []
  [:form {:method "POST"}
   [:div {:class "form-group"}
    [:input {:type "text"
             :class "form-control"
             :name "todo-text"
             :id "todo-text"
             :placeholder "Enter new todo item"}]]
   (anti-forgery-field)])

(defn nav
  [lists]
  [:nav {:class "navbar navbar-default col-md-8"}
   [:div {:class "container-fluid"}
    [:div {:class "navbar-header"}
     [:a {:class "navbar-brand"
          :href "#"}
      "TODO DO DO"]]
    [:ul {:class "nav navbar-nav navbar-right"}
     [:li {:class "dropdown"}
      [:a {:href "#"
           :class "dropdown-toggle"
           :data-toggle "dropdown"
           :role "button"
           :aria-haspopup "true"
           :aria-expanded "false"}
       "Lists"
       [:span {:class "caret"}]]
      [:ul {:class "dropdown-menu"}
       (for [list lists]
         [:li
          [:a {:href "#"}
           (:todo-list/title list)]])]]]]])

(defn todo-table
  "List the todos"
  [t-list entities]
  [:div {:class "row"}
   [:div {:class "col-md-8"}
    [:h2 (:todo-list/title t-list)]
    [:table {:class "table-striped table"}
     [:thead
      [:tr
       [:th "Todo"]
       [:th "Status"]
       [:th "Actions"]]]
     [:tbody
      (for [e entities]
        [:form {:method "POST" :action (todo-path t-list e)}
         [:input {:type "hidden"
                  :name "todo-status"
                  :id "todo-status"
                  :value (update-status e)}]
         (anti-forgery-field)
         [:tr
          [:td (maybe-wrap-strikethrough e)]
          [:td (status e)]
          [:td
           [:button {:type "submit"
                     :class "btn btn-success col-md-5"}
            (button-label e)]
           [:button {:type "submit"
                     :name "delete"
                     :value "true"
                     :class "btn btn-danger pull-right col-md-5"}
            "Delete"]]]])]]
    (new-todo-form)]])
