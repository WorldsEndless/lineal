(ns lineal.test.lineal
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]))

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

(deftest text-to-matrix
  (let [s1 "3x +4z = c"
        s2 "3x +4z = 3"
        matrix [[3 5][4 9]]]
    (testing "A nicely spaced, well-formatted string"
      (= [3 5] (text-to-row-vector "3x + 5y = 0")))
    (testing "text-row with unscaled var"
      (= [3 4 1] (text-to-row-vector s1)))))

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
      (is (not (associative-operation? - int-col)))
      (is (associative-operation? + int-col))
      (is (associative-operation? * int-col))
      (is (not (associative-operation? / int-col))))))

(deftest commutativity
  (testing "Basic Commutativity"
    (let [int-col (take 2 (filter pos? (repeatedly #(rand-int 100)))) ]
      (is (not (commutative-operation? - int-col)))
      (is (commutative-operation? + int-col))
      (is (commutative-operation? * int-col))
      (is (not (commutative-operation? / int-col))))))

(deftest distributivity
  (testing "Basic Distributivity"
    (let [int-col (take 3 (filter pos? (repeatedly #(rand-int 100)))) ]
      (testing "There is never distributivity where subtraction is involved"
        (is (not (distributive-operation? + - int-col)))
        (is (not (distributive-operation? - + int-col))))
      (testing "Addition does not distribute over multiplication"
        (is (not (distributive-operation? + * int-col))))
      (testing "Multiplication does, however, distribute over addition"
        (is (distributive-operation? * + int-col)))
      (testing "Neither should there be distributivity where division is involved"
        (is (not (distributive-operation? / + int-col)))
        (is (not (distributive-operation? + / int-col)))))))

