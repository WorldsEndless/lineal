(ns lineal.norms
  "Functions and tests for norms (especially P-Norms)

  https://en.wikipedia.org/wiki/Norm_(mathematics)#p-norm"
  (:require [clojure.math.numeric-tower :as math]))

(defn p-norm
  "Take the `p`-norm of vector `v`"
  [p v]
  (math/expt 
   (reduce + (map #(math/expt (math/abs %) p) v))
   (/ 1 p)))

(defn infinity-norm
  "Take the infinity-norm of `v`"
  [v]
  (apply max (map math/abs v)))

(defn taxicab-norm
  "One-norm"
  [v]
  (p-norm 1 v))
