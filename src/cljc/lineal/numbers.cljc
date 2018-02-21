(ns lineal.numbers
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::real number?)
(s/def ::imaginary number?)
(s/def ::complex (s/keys :req [::real ::imaginary]))

(defrecord Complex [^double real ^double imaginary])

(def REAL-NUMBERS (repeatedly #(gen/generate (s/gen ::real))))
(def COMPLEX-NUMBERS (repeatedly #(gen/generate (s/gen ::complex))))
(defn REALS [] (repeatedly #(gen/generate (s/gen ::real))))
(defn COMPLEXES [] (repeatedly #(gen/generate (s/gen ::complex))))

(defn complex?
  "Check if `n` is complex"
  [n]
  (s/valid? ::complex n))

(defn to-complex
  "Coerce to a complex number"
  [n]
  (cond
    (number? n) (Complex. n 0)
    (complex? n) n
    :else (throw (ex-info "Only numbers can be coerced to-complex" {:arg n}))))

(defn complex-conjugate
  [n]
  (cond
    (map? n) (update n ::imaginary #(* -1 %))
    (number? n) (to-complex n)
    :else (map complex-conjugate n)))
