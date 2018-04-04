(ns lineal.test.mit
  "Tests that accord with the MIT Opencourseware MIT6.241"
  (:require [clojure.test :refer :all]
            [lineal.linear :refer :all]
            [lineal.numbers :as numbers]
            [lineal.test.vector-spaces :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))
;(ns-unmap *ns* 'Vector-Space)
