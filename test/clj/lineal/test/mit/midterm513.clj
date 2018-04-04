(ns lineal.test.mit.midterm513
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]
            [lineal.numbers :as numbers]
            [lineal.test.vector-spaces :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(deftest midterm-513
  (testing "Four Fundamental Subspaces"
    (is (= 4 (count ["Domain (x)"
                     "Range (y)"
                     "Kernel (x -> right Null Space)"
                     "[left] Null Space"]))))
  (testing "Projection Theorem"
    (is (false? "The shortest distance between two vectors is the point orthogonal to the other")))
  (testing "Projection & Least Squares Estimate"
    (is false))
  (testing "Curve-fitting problem"
    (is false))
  (testing "Matrix (Induced) norms"
    (testing "Norm ||A||_1-1 is the maximum value row sum of A"
      (is false))
    (testing "Norm ||A||∞-∞ is the maximum absolute value column sum of A"
      (is false))
    (testing "Norm ||A||1-∞ is the maximum absolute value entry of A"
      (is false))
    (testing "--Frobenius Norm"
      (is false)))
  (testing "Singular Value Decomposition"
    (is false))
  (testing "Matrix Perturbations"
    (testing "Additive Perturbation"
      (is false))
    (testing "Multiplicative Perturbation"
      (is false)
      (testing "Small Gains Theorem"
        (is false))))
  (testing "Dynamic Models"
    (testing "Behavioral Models"
      (is false))
    (testing "Input-output models"
      (is false))
    (testing "Memoryless Models"
      (is false)
      (is "what's the difference between memory models and causal models?"))
    (testing "Causality"
      (is false)))
  (testing "7. State Space Models"
    (testing "Time-invariance"
      (is false))
    (testing "Linearization"
      (is false))
    (testing "Discretization"
      (is false))))
