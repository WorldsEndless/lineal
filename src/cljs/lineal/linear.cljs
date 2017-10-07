(ns lineal.linear)

;; Vectors will be considered to be column vectors, meaning that what is written in clojure as [a b c] will be equivalent to the mathematic writing of it vertically (stacked)
;; Matrixes will be considered to be sequences of vectors. With this notion, "rows" are the more difficult part of a matrix to obtain, as columns are equivalent to a vector

(defn count-columns [m]
  (count m))

(defn count-rows [m]
  (apply max (for [column m] (count column))))

(defn get-columns [m]
  (for [column m] column))

(defn get-rows [m]
  (let [num-columns (count-columns m)
        num-rows (count-rows m)]
    (for [rn (range num-rows)]
      (for [column m] (nth column rn)))))

(defn m*
  "If A = (a_{ij}) is an m x n matrix and B = (b_{ij}) is an n*r matrix then the product AB = C = (c_{ij}) is the m x r matrix whose entries are defined by c_{ij} = a(i,:)b_j = the sum of all a_{ik}b_{kj} entries from k=1 to n."
  [A B]
  (let [rows (get-rows A)
        columns (get-columns B)
        count-a-col (count (get-columns A))
        count-b-rows (count (get-rows B))]
    (if-not (= count-a-col count-b-rows)
      (throw (clojure.core/ex-info "Illegal matrix dimensions" {:type :illegal-matrix-multiplication :cause (str "Trying to multiply a " count-a-col " colum nmatrix by a " count-b-rows " rows matrix.")}))
      (for [column columns]
        (for [row rows]
          (apply +
                 (for [[ri row-item] (map-indexed vector row)]
                   (let [corresponding-item (nth column ri)]
                     (* corresponding-item row-item)))))))))
