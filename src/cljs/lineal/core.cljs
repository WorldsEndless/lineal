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

(defn drop-row [m]
  (into []
        (for [column (la/get-columns m)]
          (into [] (rest column)))))

(defn drop-column [m]
  (into [] (rest m)))

(defn add-row [m]
  (into []
        (for [column (la/get-columns m)]
          (into [] (conj column 0)))))

(defn add-column [m]
  (let [row-num (la/count-rows m)]
    (conj m (into [] (repeat row-num 0)))))


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
     [:li "And don't forget to check out "
      [:a {:href "https://github.com/WorldsEndless/lineal/blob/master/src/cljs/lineal/linear.cljs"} "the code on github"]
      " in which I've implemented what you find on this site"]]]
   [:div.row.matrix-mult
    [:div.inputs.container
     [:div.input.row
      [:div.col-md-5
       [:p.text-center.title "Matrix A"]]]
     [:div.row
      [:div.col.col-md-1.row-control
       [:div.col.stack
        [:div.row
         [:div.increase
          [:a.button.btn-success {:on-click #(swap! A add-row)}
           [:i.fa.fa-plus]]]]
        [:div.row
         [:div.explanation "Rows"]]
        [:div.row
         [:div.decrease
          [:a.button.btn-danger {:on-click #(swap! A drop-row)}
           [:i.fa.fa-minus]]]]]]
      [:div.matrix.col-md-3
       [render-dynamic-matrix A]]]
     [:div.row.col-control
      [:div.col-md-1 ""]
      [:div.col-md-1.decrease
       [:a.button.btn-danger {:on-click #(swap! A drop-column)}
        [:i.fa.fa-minus]]]
      [:div.col-md-2.explanation "Columns"]
      [:div.col-md-1.increase
       [:a.button.btn-success {:on-click #(swap! A add-column)}
        [:i.fa.fa-plus]]]]]
    [:div.inputs.container
     [:div.input.row
      [:div.col-md-5
       [:p.text-center.title "Matrix B"]]]
     [:div.row
      [:div.col.col-md-1.row-control
       [:div.col.stack
        [:div.row
         [:div.increase
          [:a.button.btn-success {:on-click #(swap! B add-row)}
           [:i.fa.fa-plus]]]]
        [:div.row
         [:div.explanation "Rows"]]
        [:div.row
         [:div.decrease
          [:a.button.btn-danger {:on-click #(swap! B drop-row)}
           [:i.fa.fa-minus]]]]]]
      [:div.matrix.col-md-3
       [render-dynamic-matrix B]]]
     [:div.row.col-control
      [:div.col-md-1 ""]
      [:div.col-md-1.decrease
       [:a.button.btn-danger {:on-click #(swap! B drop-column)}
        [:i.fa.fa-minus]]]
      [:div.col-md-2.explanation "Columns"]
      [:div.col-md-1.increase
       [:a.button.btn-success {:on-click #(swap! B add-column)}
        [:i.fa.fa-plus]]]]]
    [:div.output.row
     [:div.col-md-5
      [:p.text-center.title "Product of A and B"]]]
    [:div.row
     [:div.col-md-2 ""]
     [:div.matrix.col-md-3
      (try 
        (render-matrix (la/m* @A @B))
        (catch :default e
          (if (= :illegal-matrix-multiplication (-> e ex-data :cause))
            [:div.infobox.error (str "Illegal Matrix Multiplication: " ) ]
            [:div.error "error"])))]
     ]

    
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
