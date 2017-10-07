(ns ^:figwheel-no-load lineal.app
  (:require [lineal.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
