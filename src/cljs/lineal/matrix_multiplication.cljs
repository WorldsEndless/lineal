(ns lineal.matrix-multiplication
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [lineal.linear :as la]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [lineal.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]]))

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

(defn _render-dynamic-matrix [M]
  (let [rows (la/get-rows @M)]
    [:table.matrix
     (into [:tbody]
           (for [[row-num row] (map-indexed vector rows)]
             (into [:tr]
                   (for [[col-num item] (map-indexed vector row)] [:td
                                                               [:input {:type "number"
                                                                        :value item
                                                                        :on-change #(edit-matrix M row-num col-num (get-value %))}]]))))]))

(defn render-matrix [M]
  (let [rows (la/get-rows M)]
    [:table.matrix
     (into [:tbody]
           (for [row rows]
             (into [:tr]
                   (for [item row] [:td item]))))]))

(defn render-dynamic-matrix [matrix-atom title]
  [:div.inputs.col-md-5
   [:div.input.row
    [:div.col ;-md-5
     [:p.text-center.title title]]]
   [:div.row
    [:div.col-md-2.row-control
     [:div.stack
      [:div.row
       [:div.increase
        [:a.button.btn-success {:on-click #(swap! matrix-atom add-row)}
         [:i.fa.fa-plus]]]]
      [:div.row
       [:div.explanation "Rows"]]
      [:div.row
       [:div.decrease
        [:a.button.btn-danger {:on-click #(swap! matrix-atom drop-row)}
         [:i.fa.fa-minus]]]]]]
    [:div.matrix.col-md-3
     [_render-dynamic-matrix matrix-atom]]]
   [:div.row.col-control
    [:div
     [:div.col-md-4 ""] 
     [:div.col-md-1.decrease
      [:a.button.btn-danger.decrease {:on-click #(swap! matrix-atom drop-column)}
       [:i.fa.fa-minus]]]
     [:div.col-md-3.explanation "Columns"]
     [:div.col-md-1.increase
      [:a.button.btn-success {:on-click #(swap! matrix-atom add-column)}
       [:i.fa.fa-plus]]]]]])

(defn render-matrix-multiplication []
  [:div.matrix-multiplication
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
    [render-dynamic-matrix A "Matrix A"]
    [:div.col-md-1.swapper
     [:h1.text-center
      [:a.button.btn-success {:on-click (fn [e] (let [c @A]
                                                  (reset! A @B)
                                                  (reset! B c)))}
       [:i.fa.fa-exchange]]]]
    [render-dynamic-matrix B "Matrix B"]]
   [:div.product
    [:div.row
      [:div.col
       [:p.text-center.title "Product of A and B"]]]
    [:div.row
     [:div.matrix.col
      (try 
        (render-matrix (la/m* @A @B))
        (catch :default e
          (if (= :illegal-matrix-multiplication (-> e ex-data :type))
            [:div.infobox.error (str "Illegal Matrix Multiplication: " (-> e ex-data :cause) ) ]
            [:div.error "error"])))]
     ]]])

(defn gen-page []
  [:div.container
   [render-matrix-multiplication]])
