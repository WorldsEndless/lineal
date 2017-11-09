(ns user
  (:require 
            [mount.core :as mount]
            [lineal.figwheel :refer [start-fw stop-fw cljs]]
            [garden-gnome.watcher :as garden-gnome]
            lineal.core))

;; (mount/defstate garden
;;   :start (garden-gnome/start! (garden-gnome/default-config))
;;   :stop (garden-gnome/stop! garden))

(defn start []
  (mount/start-without #'lineal.core/repl-server))

(defn stop []
  (mount/stop-except #'lineal.core/repl-server))

(defn restart []
  (stop)
  (start))



