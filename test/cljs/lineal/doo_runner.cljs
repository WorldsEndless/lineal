(ns lineal.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [lineal.core-test]))

(doo-tests 'lineal.core-test)

