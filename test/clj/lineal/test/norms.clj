(ns lineal.test.norms
  (:require [clojure.test :refer :all]
            [clojure.math.numeric-tower :as math]
            [lineal.norms :refer :all]))

(deftest p-norms
  (testing "One-norm, aka Manhattan or Taxicab Norm"
    (is (= 6 (p-norm 1 [1 2 3]))))
  (testing "Two-norm, aka Euclidean Norm"
    (is (= (math/sqrt 14)
           (p-norm 2 [1 2 3]))))
  (testing "Infinity-norm"
    (is (= 10 (infinity-norm [1 8 4 -10])))))
