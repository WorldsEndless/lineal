(ns lineal.test.mit
  "Tests that accord with the MIT Opencourseware MIT6.241"
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]
            [lineal.numbers :as numbers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))
;(ns-unmap *ns* 'Vector-Space)
(deftest MIT-chapter-1
  (testing "Vector Spaces"
    (let [R3 (Vector-Space 3 numbers/REAL-NUMBERS)
          ;; (def R3 (Vector-Space 3 numbers/REAL-NUMBERS))
          ;; (meta R3)
          ;; => {:zero-vector [0 0 0], :add #function[lineal.linear/v+], :mult #function[lineal.linear/scale]}
          C3 (Vector-Space 3 numbers/COMPLEX-NUMBERS)
          real-continuous-functions nil
          MxN-set nil
          solution-set nil ; y^{1}(t) + 3y(t) = 0
          point-set nil ; [x1 x2 x3] in R^3 satisfying x^2_1 + x^2_2 + x^2_3 = 1 ;; these are vectors from the origin to the unit sphere
          solution-set2 nil ; y^{1}(t) + 3y(t) = sin t
          ]
      (is (vector-space? R3)) ;; true because zero vector and normalization apply, the operations are defined normally, 
      (is (vector-space? C3))
      (is (vector-space? real-continuous-functions))
      (is (vector-space? MxN-set))
      (is (vector-space? solution-set))
      (is (not (vector-space? point-set)))
      (is (not (vector-space? solution-set2))))))
