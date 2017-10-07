(ns user
  (:require 
            [mount.core :as mount]
            [lineal.figwheel :refer [start-fw stop-fw cljs]]
            lineal.core))

(defn start []
  (mount/start-without #'lineal.core/repl-server))

(defn stop []
  (mount/stop-except #'lineal.core/repl-server))

(defn restart []
  (stop)
  (start))


