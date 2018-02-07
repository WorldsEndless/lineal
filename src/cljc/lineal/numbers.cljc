(ns lineal.numbers
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::real number?)
(s/def ::imaginary number?)
(s/def ::complex (s/keys :req [::real ::imaginary]))

(defrecord Complex [^double real ^double imaginary])

(def REAL-NUMBERS (repeatedly #(gen/generate (s/gen ::real))))
(def COMPLEX-NUMBERS (repeatedly #(gen/generate (s/gen ::complex))))
