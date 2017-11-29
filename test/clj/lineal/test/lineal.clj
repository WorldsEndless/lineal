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
  (let [matrix [[3 5][4 9]]]
    (testing "A nicely spaced, well-formatted string"
      (= [3 5] (text-to-row-vector "3x + 5y = 0")))))
