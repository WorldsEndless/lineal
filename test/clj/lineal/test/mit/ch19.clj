(ns lineal.test.mit.ch19
  "Robust Stability in SISO Systems"
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]
            [lineal.numbers :as numbers]
            [lineal.test.vector-spaces :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;; Additive Perturbation
;; Multiplicative Perturbation
;; Uncertain gain

;; How shall I represent block-diagrams in Clojure?
;;; We have two types of functions: additive (when edges meet) and productive (nodes).

;; fig 19.3

(defn unity-feedback
  "Figure 19.3. Input r, output y, feedback (+ y r)"
  [r]
  (let [L (fn [r-y] r-y)]
    (-> r L))
  )

