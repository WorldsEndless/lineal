(ns lineal.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [lineal.linear :as la]
            [lineal.matrix-multiplication :as mm]
            [lineal.linearity :as linearity]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [lineal.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-dark.bg-primary
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "Lineal"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Home" :home collapsed?]
         [nav-link "#/linearity" "Linearity" :linearity collapsed?]
         [nav-link "#/matrix-multiplication" "Matrix Multiplication" :matrix-multiplication collapsed?]
         [nav-link "#/about" "About" :about collapsed?]]]])))

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to Lineal"]]
   [:div.content
    ]])

(defn about-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Matrix Multiplication and other Fundamental Operations"]]
   [:div.content 
    [:div.explanation
     "Linear algebra, transformation matrices, and systems of equations are fundamental to the language of Control Theory. By creating a program to perform basic algebraic operations, understanding of the operations can be consolidated for the programmer and understanding will be deepened by understanding the drawbacks and limitations of computational approaches. "]
    [:div.definitions
     [:ul
      [:li [:span.term "Matrix"] "A rectangular array of numbers"]
      [:li [:span.term "Vector"] "An n-tuple of real numbers."]
      [:li [:span.term "Row Vector"] "A 1 x n matrix"]
      [:li [:span.term "Column Vector"] "An n by 1 matrix."]]
     [:p
      "These are simplistic definitions. More advance definitions allow for values including not limited to real numbers, and usually assume super-imposed semantics upon the ordered values along rows and columns. This is particularly true of /transformation matrices/. "]
]]])

(def pages
  {:home #'home-page
   :about #'about-page
   :matrix-multiplication #'mm/gen-page
   :linearity #'linearity/gen-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/linearity" []
  (session/put! :page :linearity))

(secretary/defroute "/matrix-multiplication" []
  (session/put! :page :matrix-multiplication))

(secretary/defroute "/about" []
  (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
