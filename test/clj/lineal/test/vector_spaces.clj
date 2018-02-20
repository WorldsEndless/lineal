(ns lineal.test.vector-spaces
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]))

(defn associative-operation?
  "Given a poly-variadic function and collection of two or more items, determine whether the function is associative.
  
  Associativity: Changing the order in which you apply a particular associative operators DOES NOT change the result. That is, rearranging the parentheses in such an expression will not change its value. E.g. \"3+(5+2) = (3+5) + 2\"  

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


(defn distributive-over?
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

(defn fulfills-normalization-conditions?
  "Space conforms to normalization conditions: scaling components by 0 results in zero-vector, scaling by 1 is x."
  [vector-space] ;(def vector-space lineal.test.lineal/V)
  (let [{:keys [zero-vector mult]} (meta vector-space) ;{:zero-vector [0 0], :add #function[lineal.linear/v+], :mult #function[lineal.linear/scale]} (def mult scale)

        v (rand-nth vector-space)] ;(def v (rand-nth vector-space))
    (and (= zero-vector (mult v 0))
         (= v (mult v 1)))))

(defn additively-compliant?
  "Determine whether `operation` over `space` is additively compliant"
  [operation space]
  (let [{:keys [add mult]} (meta space)]
    (and
     (associative-operation? add (take 3 space)) ;; additive associativity
     (commutative-operation? add (take 2 space)) ;; additive commutativity)
     ;; TODO Additive Inverse (subtraction)
     ;; TODO Additive Closure
     ;; TODO Additive Zero
     )))

(defn multiplicatively-compliant?
  "Determine whether `operation` over `space` is multiplicatively compliant"
  [operation space]
  ;; TODO Multiplicative closure
  ;; TODO Associativity of scalar with vector multiplication (cd)*V = c(d*V)
  ;; TODO Multiplicative Unity (identity)

  )


(defn vector-space?
  "Determine whether the input collection qualifies as a vector space"
  [space] ;(def space lineal.test.mit/R3) (def add (:add (meta space))) (def mult (:mult (meta space)))
  (let [{:keys [add mult]} (meta space)
        space (if (and add mult)
                space
                (Vector-Space space))]
    (and 
     (additively-compliant? add space)
     (multiplicatively-compliant? mult space)
     (distributive-over? mult add (take 3 space)) ;; multiplicative distributivity over vector addition
     ;; TODO Multiplicative distributivity over scalar addition   
     ;;(fulfills-normalization-conditions? space)
     )))
