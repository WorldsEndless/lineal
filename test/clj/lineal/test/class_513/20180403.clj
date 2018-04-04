(ns lineal.test.class-513.20180403
  "Class on SVD, finding G matrix from a block diagram, introducing Small-Gains Theorem"
  (:require [clojure.test :refer :all]
            [lineal.test.vector-spaces :as vs]
            [lineal.norms :as n]
            [lineal.linear :refer :all]))

(deftest small-gains-theorem
  (let [M [[0 0][2 2]]
        Δ [[0 0][2 2]]
        norm (partial n/p-norm 2)]
    (is (> 1 (* (norm M) (norm Δ))))))


