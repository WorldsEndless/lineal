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

(defn clear-nil-subsets [coll]
  (filter (complement #(every? empty? %)) coll))

(defn parse-text-row [s]
  (let [la-re-parser (fn [lare]
                       (into [] (for [[_ sign coefficient variable] (clear-nil-subsets lare)]
                                  (cond
                                    (and (empty? variable)
                                         coefficient)
                                    [nil (parse-int (str sign coefficient))]

                                    (and variable (empty? coefficient))
                                    [variable 1]

                                    :default [variable
                                              (parse-int (str sign coefficient))]))))
        s (str/replace s #"\s" "")
        [left right] (vec (map (fn [s] (re-seq #"([+-])?(\d+)?([a-zA-Z])?" s)) (str/split s #"=")))
        left (la-re-parser left)
        right (->> (la-re-parser right)
                   (map (fn [[k v]] [k (* -1 v)])))
        full (into left right)
        all-map (seq-add-to-map full)]
    all-map))

(defn text-to-row-vector [s]
  (vec (map second (sort (parse-text-row s)))))

(defn system-of-strings-to-matrix [scoll]
  (let [all-maps (map parse-text-row scoll)
        all-map-keys (->> all-maps (map keys) flatten (into #{}) (into []) sort)]
    (into []
          (for [k all-map-keys]
            (into [] (for [m all-maps] (get m k 0)))))))

(defn matrix? [M?]
  (every? vector? [M? (first M?)]))

(defn single-column-matrix? [M]
  (and (matrix? M)
       (= 1 (count M))))

(defn is-vector?
  [V?]
  (and (vector? V?) (not (matrix? V?))))

(defn matrix-to-vector
  "Convert a single-column matrix to a vector"
  [M]
  (cond
    (single-column-matrix? M) (first M)
    (is-vector? M) M
    :else (throw (ex-info "Unable to vectorize"
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

(defn vector-to-matrix [v]
  [v])

(defn to-matrix
  "If coll is not a matrix (vector of vectors), try to cast it to one."
  [coll]
  (cond
    (number? coll) [[coll]]
    (matrix? coll) coll
    (is-vector? coll) (vector-to-matrix coll)
    (coll? coll) (to-matrix (vec coll))
    :default (throw (ex-info "Only valid collections can be converted to matrices."
                     {:cause "Not valid coll"
                      :input coll} ))))

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
      (throw (ex-info "Illegal matrix dimensions" {:type :illegal-matrix-multiplication :cause (str "Trying to multiply a " count-a-col " colum nmatrix by a " count-b-rows " rows matrix.")}))
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
      (throw (ex-info "Wrong arg structure" {:type :illegal-types :cause (str "Trying to add non-vector with " V1 " + " V2)})))
    (when-not (= (count V1) (count V2))
      (ex-info "Non-matching lengths of supplied vectors" {:type :invalid-input :cause (str "Length " (count V1) " is not " (count V2))}))

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

(defrecord Complex [^double real ^double imag]) ;(Complex. 3 5)

(defn associative-operation?
  "Given a poly-variadic function and collection of two or more items, determine whether the function is associative.
  
  Associativity: Changing the order in which you apply a particular associative operators DOES NOT change the result. That is, rearranging the parentheses in such an expression will not change its value. "

  ;; Part of the technical operatoin of this is in going from infix to prefix notation
  ;; TODO This is slightly fragile due to taking only two of the collection
  [function collection]
  (when (> 2 (count collection))
    (throw (ex-info "Too few items in `collection` provided to `associative-operation?`" {:collection collection})))
  ;; (= (function (first collection) (second collection))
  ;;    (function (function (first collection)) (function (second collection))))
  (= (apply function collection)
     (apply function (list (apply function (take 2 collection)) (apply function (drop 2 collection)))))
  )

(defn commutative-operation?
  "Given a function and a collection of two or more items, determine whether the function is commutative"
  [function collection]
  
  )


(defn distributive-operation?
  "Given a function and a collection of two or more items, determine whether the function is distributive"
  [function collection]
  
  )


(defn vector-space?
  "Determine whether the input collection qualifies as a vector space"
  [space & [{:keys [vector-addition-function scalar-multiplication-function]
             :or {vector-addition-function v+
                  scalar-multiplication-function m*}}]]
  (and (associative-operation? vector-addition-function (take 2 space))
       (commutative-operation? vector-addition-function (take 2 space))
       (distributive-operation?  ))
  
  ;; contains vectors
  ;; vector addition operation
  ;; scalar multiplication operation
  ;; has zero vector
  ;; normalizaton conditions 0x = 0, 1x = x
  )
