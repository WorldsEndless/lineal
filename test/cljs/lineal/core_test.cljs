(ns lineal.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [lineal.core :as rc]))

(deftest test-home
  (is (= true true)))

;; (deftest matrix-definitions
;;   (testing "Basic matrix definitions"
;;     (let [row-vector [[1] [2] [3]]
;;           column-vector [[1 2 3]]]
;;       (testing "Counts of rows and columns"
;;         (is (= 1 (count-columns column-vector)))
;;         (is (= 1 (count-rows row-vector))))
;;       (testing "Get rows"
;;         (is (= '((1 2 3)) (get-rows row-vector)))
;;         (is (= '((1) (2) (3)) (get-rows column-vector))))
;;       (testing "Get columns"
;;         (is (= '((1 2 3)) (get-columns column-vector)))
;;         (is (= '((1) (2) (3)) (get-columns row-vector)))))))

;; (deftest matrix-multiplication
;;   (testing "Column and row vectors--"
;;     (let [row-vector [[1] [2] [3]]
;;           column-vector [[1 2 3]]
;;           rv2 [[2] [4] [6]]
;;           cv2 [[2 4 6]]
;;           tall-cv [[1 2 3 4]]
;;           square-1 [[1 3][2 4]]]
;;       (testing "multiplication by row-vectors"
;;         (is (= '((14)) (m* row-vector column-vector)))
;;         (is (= '((56)) (m* rv2 cv2))))
;;       (testing "multiplication by column-vectors"
;;         (is (= '((4 8 12)(8 16 24)(12 24 36)) (m* cv2 rv2)))
;;         (is (= '((1 2 3)(2 4 6)(3 6 9)) (m* column-vector row-vector))))
;;       (testing "square vector operations"
;;         (let [A [[1 0][1 0]]
;;               B [[1 2][1 2]]]
;;           (is (= (m* A B)
;;                  '((3 0)(3 0))))
;;           (is (= (m* B A)
;;                  '((1 2)(1 2))))))
;;       (testing "from Linear Algebra with Applications"
;;         (is (= (m*
;;                 [[3 2 1] [-2 4 -3]]
;;                 [[-2 4][1 1][3 6]])
;;                '((-14 12 -14)
;;                  (1 6 -2)
;;                  (-3 30 -15)))))
;;       (testing "Mismatching dimensions"
;;         (testing "Multiplication by tall")
;;         (testing "Invalid operation: more rows than columns"
;;           (is (try
;;                 (do
;;                   (m* square-1 column-vector)
;;                   false)
;;                 (catch clojure.lang.ExceptionInfo e
;;                   (= :illegal-matrix-multiplication (-> e ex-data :type))))))))))
