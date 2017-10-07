(ns lineal.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [lineal.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[lineal started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[lineal has shut down successfully]=-"))
   :middleware wrap-dev})
