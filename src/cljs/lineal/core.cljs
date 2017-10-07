(ns lineal.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [lineal.linear :as la]
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
         [nav-link "#/about" "About" :about collapsed?]]]])))


;; multiplication
(def A (r/atom [[3 2 1] [-2 4 -3]]))
(def B (r/atom [[-2 4][1 1][3 6]]))

(defn edit-matrix [M row-num col-num val]
  (swap! M update col-num
         (fn [column] (update column row-num (constantly val)))))

(defn get-value [e]
  (.. e
      -target
      -value))

(defn render-dynamic-matrix [M]
  (let [rows (la/get-rows @M)]
    [:table.matrix
     (into [:tbody]
           (for [[row-num row] (map-indexed vector rows)]
             (into [:tr]
                   (for [[col-num item] (map-indexed vector row)] [:td
                                                               [:input {:type "number"
                                                                        :value item
                                                                        :on-change #(edit-matrix M row-num col-num (get-value %))

                                                                        }]]))))]))

(defn render-matrix [M]
  (let [rows (la/get-rows M)]
    [:table.matrix
     (into [:tbody]
           (for [row rows]
             (into [:tr]
                   (for [item row] [:td item]))))]))

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Matrix Fundamentals"]
    [:h2 "Matrix Multiplication"]
    [:p.content "Matrix multiplication of matrices A and B is performed by considering the rows of A in conjunction with the columns of B. "
     [:i "Because these rows and columns need to be considered in conjunction with each other, matrix multiplication is only defined when the number of rows in A equal the number of columns in B. "]
     "Feel free to play with the values below to see how the changes effect the matrix."]
    [:p.resources "Further recommended descriptions of how this works can be found at:"]
    [:ol
     [:li [:a {:href "https://en.m.wikipedia.org/wiki/Matrix_multiplication"} "Wikipedia"] " (Which may not be all that helpful)"]
     [:li [:a {:href "https://www.youtube.com/watch?v=kYB8IZa5AuE&index=4&list=PLZHQObOWTQDPD3MizzM2xVFitgF8hE_ab"} "Youtube: Essence of Linear Algebra"] "(Excellent!)"]
     [:li [:a {:href "https://www.amazon.com/Linear-Algebra-Applications-Featured-Introductory/dp/0321962214/ref=sr_1_1?ie=UTF8&qid=1507364868&sr=8-1&keywords=linear+algebra+with+applications"} "Textbooks like this"] " also proved useful for me, when considered in conjunction with the other explanations"]
     [:li "And don't forget to check out the code in which I've implemented the basic matrix multiplication you see below"]]]
   [:div.matrix-mult
    [:div.input "Matrix A"
     [render-dynamic-matrix A]]
    [:div.input "Matrix B"
     [render-dynamic-matrix B]]
    [:div.output
     [:div "Product of A and B"
      (render-matrix (la/m* @A @B))]]
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
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

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
