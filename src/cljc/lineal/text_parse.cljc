(ns lineal.text-parse
  "Text-to-linear algebra."
  (:require [clojure.string :as str]))

(defn parse-text-row [s]
  (let [la-re-parser (fn [lare]
                       (into [] (for [[_ sign coefficient variable] (clear-nil-subsets lare)]
                                  (cond
                                    (and (empty? variable)
                                         coefficient)
                                    [nil (parse-int (str sign coefficient))]

                                    (and variable (empty? coefficient))
                                    [variable 1]

                                    :default [variable
                                              (parse-int (str sign coefficient))]))))
        s (str/replace s #"\s" "")
        [left right] (vec (map (fn [s] (re-seq #"([+-])?(\d+)?([a-zA-Z])?" s)) (str/split s #"=")))
        left (la-re-parser left)
        right (->> (la-re-parser right)
                   (map (fn [[k v]] [k (* -1 v)])))
        full (into left right)
        all-map (seq-add-to-map full)]
    all-map))

(defn text-to-row-vector [s]
  (vec (map second (sort (parse-text-row s)))))

(defn system-of-strings-to-matrix [scoll]
  (let [all-maps (map parse-text-row scoll)
        all-map-keys (->> all-maps (map keys) flatten (into #{}) (into []) sort)]
    (into []
          (for [k all-map-keys]
            (into [] (for [m all-maps] (get m k 0)))))))
