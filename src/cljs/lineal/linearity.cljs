(ns lineal.linearity
  (:require [lineal.linear :as la]
            [lineal.matrix-multiplication :as mm]
            [reagent.core :as r]
            [clojure.string :as str]))

(def LINEAR-SYSTEM (r/atom ["3x + 4b = 0"]))

(defn sanitize [s] ;TODO: ensure space separation and alphabetical ordering of items
  ;; ensure [+ -] are space-separated
  s
  )

(defn refresh-mathjax []
  (-> js/MathJax .-Hub (.Queue (to-array ["Typeset" (.-Hub js/MathJax)]))))

(defn bmatrix [clj-matrix]
  (let [delim "$$"
        open (str delim " \\begin{bmatrix}")
        close (str " \\end{bmatrix} " delim )
        contents (str/join " \\\\ "
                           (for [row (la/get-rows clj-matrix)]
                             (str/join " & " (map str row))))]
    (str open contents close)))

(defn text-to-row-vector [s]
  (let [s (sanitize s)
        [left-side right-side] (map str/trim (str/split s #"="))
        items (str/split left-side #" ") ; now all space-separated parts, like 3a + 2b
        coefficients (filter (complement nil?)
                             (map #(re-seq  #"\d+" %) items))]
    (into [] (map (comp js/parseInt first) coefficients))))

(defn system-to-matrix [system]
  (la/get-rows
   (into [] (for [eq system] (text-to-row-vector eq)))))

(defn delete-equation [ATOM]
  (swap! ATOM drop-last))

(defn add-equation [ATOM]
  (let [default-equation "2a + 3b = 0"]
    (swap! ATOM conj default-equation)))

(defn render-linear-system
  "Render a dynamic number of inputs for linear systems" []
  (let [add-button  [:a.button.btn-success {:on-click #(add-equation LINEAR-SYSTEM)} [:i.fa.fa-plus]] 
        delete-button [:a.button.btn-danger {:on-click #(delete-equation LINEAR-SYSTEM)} [:i.fa.fa-minus]]
        rows (into [:div.equations]
                   (for [[i eq] (map-indexed vector @LINEAR-SYSTEM)]
                     [:div.row.equation
                      [:input {:type "text"
                               :value (get @LINEAR-SYSTEM i)
                               :on-change #(swap! LINEAR-SYSTEM assoc i (-> % .-target .-value))}]]))]
    [:div.linear-systems
     rows
     add-button
     delete-button]))

(defn render-matrix-linearity []
  [:div.matrix-linearity
   [:div.jumbotron
    [:h1 "Matrix Fundamentals"]
    [:h2 "Matrices and Linearity"]
    [:div.content "The \"linear\" of \"linear algebra\" refers to the fact that this algebra deals with lines. "
     [:span.strong "What's a line?"]
     " In 1-, 2-, and 3-dimensional space a line is intuitively understood. However, linear algebra is concerned with \"line-ness\" in "
     [:span.math "n"]
     " dimensions. To generalize, it is said that linear equations are those of the form:"
     [:p.math
      "$$ax + by + c = 0$$"]
     "In other words, exhibiting only addition and multiplication (understanding subtraction and division to be special cases of these two operations)."
     ;; TODO systems
     [:div.linear-systems
      [:div.row "A system of linear equations is a collection of linear equations which share context and a solution set. For example:"]
      [:div.row.equation
       [:span.math "$$a + 3b = 5\\\\
                      4a + b = 9$$"]]]
     [:p "First, let's rearrange these to be 0 on the driving side of the equation"]
     [:div.row.equation
      [:span.math "$$a + 3b - 5 = 0\\\\
                     4a + b -9 = 0$$"]]
     [:p "By working with linear equations, a variety of mathematical tools can be brought to bear. Importantly, any linear equation can be represented as a matrix. A system of linear equations can be represented as a matrix. We do this by taking the coefficients of each variable and stacking them, dropping any free scalars:"]
     [:p.matrixify (bmatrix [[1 4][3 1]])]
     [:p "In this way, any linear system of equations can be represented as a matrix."]]]
   [:div.content
    [:div.instructions "Enter a system of linear equations below to view it as a matrix."]
    [render-linear-system]
    ;[mm/render-matrix (system-to-matrix @LINEAR-SYSTEM)]
    ]])


(defn gen-page []
  [:div.container
   [render-matrix-linearity]])

;;MathJax.Hub.Queue(["Typeset",MathJax.Hub]);
