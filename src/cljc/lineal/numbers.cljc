(ns lineal.numbers
  (:require [clojure.spec.alpha :as s]))

(s/def ::real number?)
(s/def ::imaginary number?)
(s/def ::complex (s/keys :req [::real ::imaginary]))
(s/def ::goober string?)

(defrecord Complex [^double real ^double imaginary])
