(ns lineal.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[lineal started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[lineal has shut down successfully]=-"))
   :middleware identity})
