(ns lineal.test.mit
  "Tests that accord with the MIT Opencourseware MIT6.241"
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]))

(deftest "Chapter 1"
  
  )

(deftest MIT-chapter-1
  (testing "Vector Spaces"
    (let [REALS [1 100 -35 0.5 -24 (/ 1000 34.9)]
          COMPLEX [(Complex. 3 0) (Complex. 100 3) (Complex. -3 25)]
          real-continuous-functions nil
          MxN-set nil
          solution-set nil ; y^{1}(t) + 3y(t) = 0
          point-set nil ; [x1 x2 x3] in R^3 satisfying x^2_1 + x^2_2 + x^2_3 = 1 ;; these are vectors from the origin to the unit sphere
          solution-set2 nil ; y^{1}(t) + 3y(t) = sin t
          ]
      (is (vector-space? REALS)) ;; true because zero vector and normalization apply, the operations are defined normally, 
      (is (vector-space? COMPLEX))
      (is (vector-space? real-continuous-functions))
      (is (vector-space? MxN-set))
      (is (vector-space? solution-set))
      (is (not (vector-space? point-set)))
      (is (not (vector-space? solution-set2))))))
