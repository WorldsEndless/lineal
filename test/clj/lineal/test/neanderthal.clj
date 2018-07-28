(ns lineal.test.neanderthal
  (:require [clojure.test :refer :all]
            [uncomplicate.neanderthal.core :as nc]
            [uncomplicate.neanderthal.native :as nn]
            [uncomplicate.neanderthal.linalg :as la]
            [uncomplicate.neanderthal.math :as nm]
            [uncomplicate.fluokitten.core :as fc]
            [lineal.config :refer [env]]
            [lineal.norms :as n]))

(defn round2
  "Round a double to the given precision (number of significant digits)"
  [precision d]
  (let [factor (Math/pow 10.0 precision)]
    (/ (Math/round (* d factor)) factor)))

(defn within-margin
  "Determine whether `value` is withing `target` of `margin`"
  [margin target value]
  (let [bottom (- target margin)
        top (+ target margin)]
    (> top value bottom)))

(defn every-f=
  "Take two sequences and determine whether each element is f= the element of the other "
  [col1 col2]
  (every? true?
          (for [[k v] (map-indexed vector col1)]
            (nm/f= v (nth col2 k)))))

(defn all-round
  "Round all contents of `coll` (recursively)to `precision`"
  [precision coll]
  (for [i coll] (if (coll? i)
                  (all-round precision i)
                  (round2 precision i))))


(deftest vector-spaces
  (let [source-url "https://dragan.rocks/articles/17/Clojure-Linear-Algebra-Refresher-Vector-Spaces"
        v1 (nn/dv -1 2 5.2 0)
        v2 (nn/dv (range 22))
        v3 (nn/dv -2 -3 1 0 )]
    (testing ">> Types of vectors"
      (is (= uncomplicate.neanderthal.internal.host.buffer_block.RealBlockVector
             (type v1)
             (type v2))))
    (testing "Vector addition"
      (is (= "incompatible vector-size"
             (try (nc/xpy v1 v2)
                  (catch Exception e "incompatible vector-size"))))
      (is (nc/vctr? (nc/xpy v1 v3))))
    (testing "Vector multiplication"
      (is (= (nc/scal 2 v1)
             (nc/scal 4 (nc/scal 0.5 v1)))))
    (testing "Zero vectors"
      (is (every? zero? (nc/zero v2)))
      (is (= 22 (nc/dim (nc/zero v2)))))
    (testing "Vector negative"
      (is (every? zero? (nc/axpy -1 v3 v3))))
    (testing "Linear Combinations"
      (let [u (nn/dv 2 5 -3)
            v (nn/dv -4 1 9)
            w (nn/dv 4 0 2)]
        (is (= (nn/dv 20 7 -31)
               (nc/axpy 2 u -3 v 1 w)))))
    (testing "Column and Row Vectors"
      (is true (boolean "irrelevant in Neanderthal")))
    (testing "Dot Products"
      (let [u (nn/dv 1 -2 4)
            v (nn/dv 3 0 2)]
        (is (= 11.0 (nc/dot u v)))))
    (testing "Norms"
      (is (> 5.92 (nc/nrm2 (nn/dv 1 3 5)) 5.91)))
    (testing "Normalizing"
      (let [v (nn/dv 1 3 5)]
        (is (every? #(> -0.0 % -0.01)
                    (nc/axpy -1 (nn/dv 0.17 0.51 0.85)
                             (nc/scal (/ (nc/nrm2 v))
                                      v))))))
    (testing "Angle between vectors"
      (let [u (nn/dv 1 0 0)
            v (nn/dv 1 0 1)]
        (is (> 0.708
               (/ (nc/dot u v) (nc/nrm2 u) (nc/nrm2 v))
               0.7071))
        (is (= 1.0 (/ (nc/dot u u) (nc/nrm2 u) (nc/nrm2 u))))))
    (testing "Orthogonal vectors: if dot of vectors is 0, orthogonal"
      (is (= 0.0 (nc/dot
                  (nn/dv 2 -3 1)
                  (nn/dv 1 2 4)))))
    (testing "Distance between points"
      (let [x (nn/dv 4 0 -3 5)
            y (nn/dv 1 -2 3 0)
            distance-xy 8.602]
        (is (within-margin 0.0004 distance-xy (nc/nrm2 (nc/axpy -1 y x))))))))

(deftest matrices
  (let [source-url   "https://dragan.rocks/articles/17/Clojure-Linear-Algebra-Refresher-Vector-Spaces"
        m (nn/dge 2 3 (range 6))]
    (is (within-margin 0.001 7.4161 (nc/nrm2 m)))))

(deftest system-of-equations
  (testing "Linear Independence"
    (let [cs (nn/dge 3 1)
          m (nn/dge 3 3 [1 2 0
                         0 1 -1
                         1 1 2]) ]
      (is (= (nn/dge 3 1 [0.0 -0.0 -0.0]) (la/sv m cs))))))

(deftest rank-svd
  (let [a (nn/dge 4 4 [1 0 0 0
                    2 0 0 0
                    0 1 0 0
                       0 0 1 0])
        round (comp (partial all-round 2) flatten seq)]
    (is (every-f= (round (nn/dgd 4 [2.24 1.0 1.0 0.0]))
                  (round (:sigma (la/svd a)))))))

(deftest projection
  (testing "Projection of v onto u"
    (let [v (nn/dv 6 7)
          u (nn/dv 1 4)]
      (is (= (nn/dv 2.0 8.0) 
             (nc/scal (/ (nc/dot u v) (nc/dot u u))
                      u))))))

(deftest eigenstuff
  (let [source-url "https://dragan.rocks/articles/17/Clojure-Linear-Algebra-Refresher-Eigenvalues-and-Eigenvectors"
        a (nn/dge 2 2 [-4 3 -6 5])
        eigenvectors (nn/dge 2 2)
        eigenvalues (la/ev! (nc/copy a) nil eigenvectors)
        lambda1 (nc/entry eigenvalues 0 0)
        x1 (nc/col eigenvectors 0)
        lambda2 (nc/entry eigenvalues 1 0)
        x2 (nc/col eigenvectors 1)]
    (testing "Expected eigenvalue"
      (is (= -1.0 lambda1)))
    (testing "Really an eigenvector?"
      (is (every? (partial nm/f= 0.00)
                  (seq (nc/axpy -1
                                (nc/mv a x1)
                                (nc/scal lambda1 x1))))))
    (testing "Normalized eigenvectors"
      (is (= 1.0 (nc/nrm2 x1)))
      (is (nm/f= 1.0 (nc/nrm2 x2))))
    (testing "Expected second eigenvalue"
      (is (nm/f= 2.0 lambda2)))
    (testing "expected eigenvector"
      (is (every-f= (list 0.707 -0.707) (map (partial round2 3)
                                             (seq x2)))))))
(deftest Diagonalization
  (testing "B=C^{-1}AC"
    (let [A (nn/dge 2 2 [7 3 -10 -4])
          C (nn/dge 2 2 [2 1 5 3])
          inv-C (la/tri! (la/trf C))]
      (is (= '((2.0 0.0)
               (0.0 1.0))
             (-> (nc/mm inv-C
                        (nc/mm A C))
                 seq)))))
  (testing "Eigen-diagonal Matrix D"
    (let [A (nn/dge 2 2 [-4 3 -6 5])
          evec (nn/dge 2 2)
          eval (la/ev! (nc/copy A) nil evec)
          inv-evec (la/tri! (la/trf evec))
          diag (nc/col eval 0)
          d (nc/mm inv-evec (nc/mm A evec))
          round (partial all-round 2)]
      (is (= (round '((-1.00 0.00)
                      (0.00 2.00)))
             (round (seq d))))
      (testing "Diagonal for Matrix to-exponents"
        (let [d9 (nc/copy d)
              fd9 (fc/fmap! (nm/pow 9)
                            (nc/dia d9))]
          (is (= (round (seq (nn/dge 2 2 [-1.0 0.0 0.0 512.0])))
                 (round (seq d9)))))))))

(deftest matrix-transformations
  (let [source-url "https://dragan.rocks/articles/17/Clojure-Linear-Algebra-Refresher-Matrix-Transformations"]
    (testing "Use a matrix to scale a vector (dilation)"
      (let [scale-matrix (nn/dge 2 2 [3 0 0 3])
            v (nn/dv 1 2)]
        (is (= '(3.0 6.0)
               (seq (nc/scal 3 v))
               (seq (nc/mv scale-matrix v))))))
    (testing "Reflection along x-axis"
      (let [v (nn/dv 3 2)
            reflection-matrix (nn/dge 2 2 [1 0 0 -1])]
        (is (= '(3.00 -2.00)
               (->> v (nc/mv reflection-matrix) seq)))))
    (testing "Rotation about origin by pi/2 angle"
      (let [v (nn/dv 3 2)
            sinpihalf 1
            cospihalf 0
            rotation-matrix (nn/dge 2 2 [cospihalf sinpihalf
                                         (- sinpihalf) cospihalf])]
        (is (= '(-2.00 3.00) 
               (seq (nc/mv rotation-matrix v))))))
    (testing "define unit-square of transformation"
      (let [A (nn/dge 2 2 [4 2 2 3])
            p (nn/dv 1 0)
            q (nn/dv 1 1)
            r (nn/dv 0 1)
            o (nn/dv 0 0)
            original-unit-square [p q r o]
            answer '((4.0 2.0)
                     (6.0 5.0)
                     (2.0 3.0)
                     (0.0 0.0))]
        (is (= answer
               (map (comp seq (partial nc/mv A)) original-unit-square)))
        (testing ">> The A matrix is invertible"
          (is (not (zero? (la/det (la/trf A))))))
        (testing ">> Same result utilizing matrices instead of dumb vectors" 
          (let [square (nn/dge 2 4 [1 0 1 1 0 1 0 0])]
            (is (= answer (seq (nc/mm A square))))))))
    (testing "Matrix as function composition"
      (let [pi2 (/ nm/pi 2)
            reflection (nn/dge 2 2 [1 0 0 -1])
            rotation (nn/dge 2 2 [(nm/cos pi2) (nm/sin pi2)
                                  (- (nm/sin pi2)) (nm/cos pi2)])
            dilation (nn/dge 2 2 [3 0 0 3])
            full-comp (nc/mm dilation rotation reflection)
            rnd (comp (partial all-round 1) seq)]
        (is (= '((0.0 3.0) (3.0 -0.0)) (rnd full-comp)))
        (is (= '(6.0 3.0)
               (rnd (nc/mv full-comp (nn/dv 1 2))))))))
  (testing "Orthogonal Transformations"
    (testing ">> Preserve rigid body-shapes, inc. norms, angles, distances"
      (let [sqrt12 (nm/sqrt 0.5)
            A (nn/dge 2 2 [sqrt12 (- sqrt12)
                           sqrt12 sqrt12])            
            u (nn/dv 2 0)
            v (nn/dv 3 4)
            tu (nc/mv A u)
            tv (nc/mv A v )
            angle (fn [v1 v2]
                    (/ (nc/dot v1 v2)
                       (* (nc/nrm2 v1) (nc/nrm2 v2))))
            distance (fn [v1 v2]
                       (nc/nrm2 (nc/axpy -1 v1 v2)))]
        (testing ">> Preserve norms"
          (is (= (nc/nrm2 u) (nc/nrm2 tu))))
        (testing ">> Preserve angles"
          (is (nm/f= (angle u v)
                     (angle tu tv))))
        (testing ">> Preserve distances"
          (is (nm/f= (distance u v)
                     (distance tu tv))))
        (testing "Vector transformations"
          (testing ">> Translation, so translated is nearer to v2 than untranslated"
            (let [v1 (nn/dv 1 2)
                  v2 (nn/dv 4 2)]
              (is (nm/f< (distance (nc/axpy v1 v2) v2)
                         (distance v1 v2)))))
          (testing ">> Affine Transformations"
            (is false)))))))


  
