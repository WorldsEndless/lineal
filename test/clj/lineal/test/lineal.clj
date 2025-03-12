(ns lineal.test.lineal
  (:require [clojure.test :refer :all]
            [lineal.test.vector-spaces :as vs]
            [lineal.linear :refer :all]
            [lineal.linear-algebra.core :as la]))

(defn random-vector-space
  "Generate a random vector"
  []
  (let [int-pool (range -100 100)
        space-dimensionality (inc (rand-int 4))]
    (Vector-Space
     (repeatedly space-dimensionality #(repeatedly space-dimensionality (fn [] (rand-nth int-pool)))))))

(deftest matrix-multiplication
  (testing "Leon 3 ed. pg. 25-26, section 3"
    (testing "Example 2"
      (let [A (->Matrix [[-3 2 4] [1 5 2]])
            B (->Matrix [[2 4]])
            solution [[-2 24 16]]] ;(def solution [[-2 24 16]])
        (testing "Basic m*"
          (is (= solution (m* A B))))
        (testing "Matrix is a function, too"
          (is (= solution (A B) )))))))


(deftest Denton-chapter-1
  (testing "Reading 1: Commutative vector addition"
    (let [M [[1 3 5] [4 6 8] [2 8 16]]
          V1 [3 6 9]
          V2 [1 2 3]]
      (is
       (apply = (map matrix-to-vector [(m* M (v+ V1 V2 ))
                                       (v+ (m* M V1) (m* M V2))])))))
  (testing "Reading 2: Associative Matrix Multiplication"
    (let [M [[2 4][6 8]]
          N [[3 5][7 9]]
          V [10 15]]
      (is (= (m* M (m* N V))
             (m* (m* M N) V))))))

;; (deftest text-to-matrix
;;   (let [s1 "3x +4z = c"
;;         s2 "3x +4z = 3"
;;         matrix [[3 5][4 9]]]
;;     (testing "A nicely spaced, well-formatted string"
;;       (= [3 5] (text-to-row-vector "3x + 5y = 0")))
;;     (testing "text-row with unscaled var"
;;       (= [3 4 1] (text-to-row-vector s1)))))

(deftest linearity
  (testing "Linear function (Denton pg 37)"
    (let [f [[3 5][4 9]]
          x [1 2 3 4]
          y [2 4 6 8]
          x-plus-y (v+ x y)
          fxy (v+ (m* f x) (m* f y)) ; these are single-col matrices, not vectors
          fxy2 (m* x-plus-y)]
      (is (= fxy fxy2)))))

(deftest associativity
  (testing "Basic Associativity"
    (let [int-col (take 10 (filter pos? (repeatedly #(rand-int 100)))) ]
      (is (not (vs/associative-operation? - int-col)))
      (is (vs/associative-operation? + int-col))
      (is (vs/associative-operation? * int-col))
      (is (not (vs/associative-operation? / int-col))))))

(deftest commutativity
  (testing "Basic Commutativity"
    (let [int-col (take 2 (filter pos? (repeatedly #(rand-int 100)))) ]
      (is (not (vs/commutative-operation? - int-col)))
      (is (vs/commutative-operation? + int-col))
      (is (vs/commutative-operation? * int-col))
      (is (not (vs/commutative-operation? / int-col))))))

(deftest distributivity
  (testing "Basic Distributivity"
    (let [int-col (take 3 (filter pos? (repeatedly #(rand-int 100)))) ]
      (testing "There is never distributivity where subtraction is involved"
        (is (not (vs/distributive-over? + - int-col)))
        (is (not (vs/distributive-over? - + int-col))))
      (testing "Addition does not distribute over multiplication"
        (is (not (vs/distributive-over? + * int-col))))
      (testing "Multiplication does, however, distribute over addition"
        (is (vs/distributive-over? * + int-col)))
      (testing "Neither should there be distributivity where division is involved"
        (is (not (vs/distributive-over? / + int-col)))
        (is (not (vs/distributive-over? + / int-col)))))))

(deftest zero-vec
  (testing "Zero-Vector creation"
    (are [x] (vs/zero-vector? x)
      (Zero-Vector 1)
      (Zero-Vector 33)
      (Zero-Vector [1 2 3]))
    (testing "Invalid to Zero-Vector"
      (is
       (try
         (not (vs/zero-vector? (Zero-Vector -3)))
         (catch Exception e true)))))
  (testing "zero-vector?"
    (let [zv (vec (repeat (rand-int 20) 0))]
      (is (vs/zero-vector? zv))
      (is (not (vs/zero-vector? (conj zv 1)))))))


(deftest normalization
  (let [V (random-vector-space)] ;(def V (random-vector-space))
    (is (vs/fulfills-normalization-conditions? V))
    (is (not (vs/fulfills-normalization-conditions?
              (with-meta V (merge (meta V) {:zero-vector nil})))))
    (is (not (vs/fulfills-normalization-conditions?
              (with-meta V (merge (meta V) {:zero-vector [9]})))))))

(deftest infinite-vectorspaces
  (let [infinite-vector (range)]
    (testing "Creating a vectorspace around infinite vectors"
      (is false)
      )))

(deftest linearization
  (testing "Linearize a system"
    (is false)))

(defn random-vector
  "Generate random integer vector, of length `n` if given, with values max `m` if given"
  ([] (repeatedly #(* (rand-nth [1 -1]) (rand-int 1000))))
  ([n] (random-vector n 1000))
  ([n m] (vec (repeatedly n #(* (rand-nth [1 -1]) (rand-int m))))))

(deftest inner-product
  (testing "The Euclidian (dot-product) is a valid inner-product"
    (is (la/inner-product? dot-product)))
  (testing "Vector addition is not a valid inner-product"
    (is (la/inner-product? v+))))
