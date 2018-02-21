(ns lineal.linear
  (:require
   [clojure.string :as str]
   [lineal.numbers :as num]))

;; Vectors will be considered to be column vectors, meaning that what is written in clojure as [a b c] will be equivalent to the mathematic writing of it vertically (stacked)
;; Matrixes will be considered to be sequences of vectors. With this notion, "rows" are the more difficult part of a matrix to obtain, as columns are equivalent to a vector

(defn sanitize [s] ;TODO: ensure space separation and alphabetical ordering of items
  ;; ensure [+ -] are space-separated
  s)

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



(defn matrix? [M?]
  (every? sequential? [M? (first M?)]))

(defn single-column-matrix? [M]
  (and (matrix? M)
       (= 1 (count M))))

(defn is-vector?
  [V?]
  (and (sequential? V?) (not (matrix? V?))))

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
  "Vector addition"
  ([] nil)
  ([V] V)
  ([V1 V2]
   (let [[V1 V2] (map #(if (single-column-matrix? %) (matrix-to-vector %) %) [V1 V2]) ]
     (when-not (every? is-vector? (list V1 V2))
       (throw (ex-info "Wrong arg structure" {:type :illegal-types :cause (str "Trying to add non-vector with " V1 " + " V2)})))
     (when-not (= (count V1) (count V2))
       (ex-info "Non-matching lengths of supplied vectors" {:type :invalid-input :cause (str "Length " (count V1) " is not " (count V2))}))

     (for [[i v] (map-indexed vector V1)]
       (+ v (nth V2 i)))))
  ([V1 V2 & rest]
   (reduce v+ (v+ V1 V2) rest)))

(defn v*
  "Multiply a vector point-wise against another"
  [v1 v2]
  (cond
    (and (if (instance? clojure.lang.IPending v1)
           (realized? v1)
           true)
         (if (instance? clojure.lang.IPending v2)
           (realized? v2)
           true)
         (not= (count v1) (count v2)))
    (throw (ex-info "non-matching vectors given to v*" {:args {:v1 v1 :v2 v2}}))
    
    :else
    (let [interleaved (interleave v1 v2)]
      (map #(apply * %) (partition 2 interleaved)))))

(defn dot-product
  "Take the dot-product (Euclidean product) between `v1` and `v2`"
  [v1 v2] (apply + (v* v1 v2)))
                                        
(defn scale
  "Scale a vector"
  [vec scalar]
  (for [v vec] (* v scalar)))

(defn matrix-swap [M from to]
  (let [orig-to (M to)]
    (-> M (assoc to (M from)) (assoc from orig-to))))

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
                                        ;(first content)
                                        ;(Zero-Vector 2)
                                        ;(Zero-Vector content)

(defmulti Vector-Space ;; This is a multimethod because it needs to be poly-variadic yet still sensitive to the first value
  "Create a vector space given a function or (possibly lazy) flat collection.
  Given `n`, internal vectors will be `n` length from the given lazy seq.
  Default `n` is 2. 
  
  E.g. the R^3 space would be `(Vector-Space 3 REAL-NUMBERS)`"
  (fn [n-or-content content-or-args & argmap] (type n-or-content)))
                                        ;(ns-unmap *ns* 'Vector-Space)
(defmethod Vector-Space (type 111)
  [n content & [{:keys [zero-vector ;(def content2 num/REAL-NUMBERS)
                        vector-addition-function
                        vector-scale-function]
                 :or {zero-vector nil
                      vector-addition-function v+
                      vector-scale-function scale}}]]
  (if (sequential? content)
    (let [content (map vec (partition n content))] ;(def content (map vec (partition 3 content))) (take 2 content)
      (with-meta content {:zero-vector (or zero-vector (Zero-Vector (first content)))
                          :add vector-addition-function
                          :mult vector-scale-function}))
    (throw (ex-info "Invalid `content` for Vector-Space" {:content-type (type content)} :invalid-type))))

(defmethod Vector-Space :default ;(type num/REAL-NUMBERS) ;; lazy stuff
  [content & [argmap]]
  (Vector-Space 2 content argmap))

(defn linearize
  "Linearize a system"
  [system]
  (let [equilibrium-point nil]
    ))

(defn transpose-matrix [m]
  (vec (map vec (get-rows m))))

(defn complex-conjugate-transpose
  [complex-matrix]
  (num/complex-conjugate (transpose-matrix complex-matrix)))

;(vec (map vec (partition 3 (take 9 num/COMPLEX-NUMBERS))))
