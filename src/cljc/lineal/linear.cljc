(ns lineal.linear
  (:require
   [clojure.string :as str]
   [lineal.numbers :as num]))

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
  (into [] (for [v vec] (* v scalar))))

(defn matrix-swap [M from to]
  (let [orig-to (M to)]
    (-> M (assoc to (M from)) (assoc from orig-to))))

(defn associative-operation?
  "Given a poly-variadic function and collection of two or more items, determine whether the function is associative.
  
  Associativity: Changing the order in which you apply a particular associative operators DOES NOT change the result. That is, rearranging the parentheses in such an expression will not change its value.

  https://en.wikipedia.org/wiki/Associative_property"

  ;; Part of the logical challenge of this operation is in going from infix to prefix notation
  [function collection]
  (when (> 3 (count collection))
    (throw (ex-info "Too few items in `collection` provided to `associative-operation?`" {:collection collection})))
  ;; (= (function (first collection) (second collection))
  ;;    (function (function (first collection)) (function (second collection))))
  (= (apply function collection)
     (apply function (list (apply function (take 2 collection)) (apply function (drop 2 collection))))))

(defn commutative-operation?
  "Given a function and a collection of two items, determine whether the function is commutative.

  Changing the order in which operands are sequenced through a commutative binary function DOES NOT change the result.  \"3 + 4 = 4 + 3\" or \"2 × 5 = 5 × 2\"

  https://en.wikipedia.org/wiki/Commutative_property"
  [function [c1 c2 :as  collection]]
  (when (not= 2 (count collection))
    (throw (ex-info " Commutativity refers to binary operations. More than two items provided to `commutative-operation?`" {:collection collection})))
  (= (function c1 c2)
     (function c2 c1)))


(defn distributive-operation?
  "Given a pair of two-variadic functions and a collection of three values, determine whether the function is distributive.

   To multiply a sum (or difference) by a factor, each summand (or minuend and subtrahend) is multiplied by this factor and the resulting products are added (or subtracted).

  Example: 2 ⋅ (1 + 3) = (2 ⋅ 1) + (2 ⋅ 3), but 2 / (1 + 3) ≠ (2 / 1) + (2 / 3).

  https://en.wikipedia.org/wiki/Distributive_property"
  [outer-function inner-function [c1 c2 c3 :as  collection]]
  (when (not= 3 (count collection))
    (throw (ex-info "Distributivity refers to pairs of binary operations. More than three items provided to `commutative-operation?`" {:collection collection})))
  (= (outer-function c1 (inner-function  c2 c3))
     (inner-function (outer-function c1 c2) (outer-function c1 c3))))


(defn zero-vector?
  "Determine if the given vector is a 0 vector, or if the first 1000 components are 0.
  Zero vectors can have any number of 0s, but will have nothing else."
  [v]
  (every? #(= 0 %) (take 1000 v)))

(defn has-zero-vector?
  "The space has a zero vector `z` such that every vector `v` in the space has (= `v` (+ `v` `z`))"
  [vector-space]
  (let [zv (:zero-vector (meta vector-space))
        v+ (:vector-addition-function (meta vector-space))
        sample-v (rand-nth vector-space)]
    (when (and zv m*)
      (and
       (zero-vector? zv)
       (= sample-v (v+ zv sample-v))))))

(defn Zero-Vector
  "Produces a zero vector of size `n`, or of size matching input count"
  [n-or-count]
  (let [n (cond
            (and (integer? n-or-count)
                 (pos? n-or-count))
            n-or-count
            
            (sequential? n-or-count) (count n-or-count)
            
            :else (throw (ex-info (str "Invalid arg to `Zero-Vector`: " n-or-count)
                                  {:arg n-or-count})))]
    (vec (repeat n 0))))

;(Zero-Vector 0)

(defn Vector-Space
  "Create a vector spacegiven a function or collection"
  [content & [{:keys [zero-vector
                      vector-addition-function
                      vector-scale-function]
               :or {zero-vector (Zero-Vector (first content))
                    vector-addition-function v+
                    vector-scale-function scale}}]]
  (if (sequential? content)
    (with-meta content {:zero-vector zero-vector
                        :add vector-addition-function
                        :mult vector-scale-function})
    (throw (ex-info "Invalid `content` for Vector-Space" {:content-type (type content)} :invalid-type))))

(defn fulfills-normalization-conditions?
  "Space conforms to normalization conditions: scaling components by 0 results in zero-vector, scaling by 1 is x."
  [vector-space] ;(def vector-space lineal.test.lineal/V)
  (let [{:keys [zero-vector mult]} (meta vector-space) ;{:zero-vector [0 0], :add #function[lineal.linear/v+], :mult #function[lineal.linear/scale]} (def mult scale)

        v (rand-nth vector-space)] ;(def v (rand-nth vector-space))
    (and (= zero-vector (mult v 0))
         (= v (mult v 1)))))


(defn vector-space?
  "Determine whether the input collection qualifies as a vector space"
  [space]
  (let [{:keys [add mult]} (meta space)
        space (if (and add mult)
                space
                (Vector-Space space))]
    (and (associative-operation? add (take 2 space))
         (commutative-operation? add (take 2 space))
         (associative-operation? mult (take 2 space))
         (distributive-operation? mult add (take 3 space))
         (has-zero-vector? space)
         (fulfills-normalization-conditions? space))))
