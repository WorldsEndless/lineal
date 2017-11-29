(ns lineal.linear
  (:require
   [clojure.string :as str]))

;; Vectors will be considered to be column vectors, meaning that what is written in clojure as [a b c] will be equivalent to the mathematic writing of it vertically (stacked)
;; Matrixes will be considered to be sequences of vectors. With this notion, "rows" are the more difficult part of a matrix to obtain, as columns are equivalent to a vector

(defn sanitize [s] ;TODO: ensure space separation and alphabetical ordering of items
  ;; ensure [+ -] are space-separated
  s
  )

(def parse-int
  #?(:cljs (fn [s] (js/parseInt s))
     :clj (fn [s] (Integer/parseInt s))))

;; (defn text-to-row-vector [s]
;;   (let [s (sanitize s)
;;         [left-side right-side] (map str/trim (str/split s #"="))
;;         items (str/split left-side #" ") ; now all space-separated parts, like 3a + 2b
;;         coefficients (filter (complement nil?)
;;                              (map #(re-seq  #"\d+" %) items))]
;;     (into [] (map (comp parse-int first) coefficients))))

(defn linear-equation? [s]
  (and 
   (> 2 (count (re-seq #"=" s))) ; only one =
   (not (re-find #"[a-zA-Z][a-zA-Z]" s)) ; no letter-combos
   (not (re-find #"[*~^%$_]" s))
   (not (re-find #"[+\-]{2,}" s))))

(defn nil+ [o & rest] (let [o (or o 0)]
                        (apply + o rest)))

(defn map-adding [om & [[k v]]] (update om k nil+ v))

(defn seq-add-to-map
  ([kv-seq] (seq-add-to-map {} kv-seq))
  ([m kv-seq]
   (reduce map-adding m (seq kv-seq))))

(defn parse-text-row [s]
  (let [s (str/replace s #"\s" "")
        [left right] (vec (map (fn [s] (re-seq #"([+-])?(\d+)?([a-zA-Z])?" s)) (str/split s #"=")))
        left (into [] (for [s left] [(when-let [s2 (nth s 2)]
                                       (parse-int s2))
                                     (last s)]))
        right (into [] (for [s right] [(when-let [s2 (nth s 2)]
                                         (* -1 (parse-int s2)))
                                       (last s)]))
        full (into left right)
        sanitized (map reverse (filter (complement #(every? nil? %)) full))
        all-map (seq-add-to-map sanitized)]
    all-map))

(defn text-to-row-vector [s]
  (vec (map first (parse-text-row s))))

(defn system-of-strings-to-matrix [scoll]
                                        ; first, to map of parse-text-row
  (let [all-maps (into [] (for [s scoll]
                            (into {} (for [[v k]
                                           (parse-text-row s)] [k v]))))
        all-map-keys (into #{} (flatten (map keys all-maps)))
        rows (for [m all-maps k all-map-keys]
               (get m k)
               )]
    rows
    )
                                        ; each string gets a map with var:val, then construct the matrix by considering the vars of each map
)

(defn matrix? [M?]
  (when (coll? (first M?)) M?))

(defn single-column-matrix? [M]
  (and (matrix? M)
       (= 1 (count M))))

(def is-vector? (complement matrix?))

(defn matrix-to-vector
  "Convert a single-column matrix to a vector"
  [M]
  (cond
    (single-column-matrix? M) (first M)
    (is-vector? M) M
    :else (throw (clojure.core/ex-info "Unable to vectorize"
                                       {:type :illegal-types
                                        :cause (str "Not a matrix: " M)}))))

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

(defn vector-to-matrix [V] [V])

(defn m*
  "If A = (a_{ij}) is an m x n matrix and B = (b_{ij}) is an n*r matrix then the product AB = C = (c_{ij}) is the m x r matrix whose entries are defined by c_{ij} = a(i,:)b_j = the sum of all a_{ik}b_{kj} entries from k=1 to n."
  [A B]
  (let [A (if (is-vector? A) (vector-to-matrix A) A)
        B (if (is-vector? B) (vector-to-matrix B) B)
        rows (get-rows A)
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

(defn v+
  [V1 V2]
  (let [[V1 V2] (map #(if (single-column-matrix? %) (matrix-to-vector %) %) [V1 V2]) ]
    (when-not (every? is-vector? (list V1 V2))
      (throw (clojure.core/ex-info "Wrong arg structure" {:type :illegal-types :cause (str "Trying to add non-vector with " V1 " + " V2)})))
    (when-not (= (count V1) (count V2))
      (clojure.core/ex-info "Non-matching lengths of supplied vectors" {:type :invalid-input :cause (str "Length " (count V1) " is not " (count V2))}))

    (for [[i v] (map-indexed vector V1)]
      (+ v (nth V2 i)))))

(defn scale
  "Scale a vector"
  [vec scalar]
  (into (empty vec)
        (for [v vec] (* v scalar))))

(defn matrix-swap [M from to]
  (let [orig-to (M to)]
    (-> M (assoc to (M from)) (assoc from orig-to))))
