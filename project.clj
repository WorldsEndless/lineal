(defproject lineal "0.1.0-SNAPSHOT"

  :description "Learning linear algebra test application"
  :url "http://toryanderson.com/lineal"
  :dependencies [[clj-time "0.15.2"]
                 [funcool/struct "1.4.0"]
                 [mount "0.1.21"]
                 [org.clojure/clojure "1.12.0"]
                 [org.clojure/clojurescript "1.11.132"]
                 [org.clojure/tools.cli "1.1.230"]
                 [org.webjars.npm/mathjax "4.0.0-alpha.1"]
                 [org.clojure/math.numeric-tower "0.1.0"]
                 [uncomplicate/neanderthal "0.53.2"]
                 ; [complex "0.1.11"] ; java strong implementation for complex numbers re: Apache
                 ]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot lineal.core

  :plugins [[lein-cprop "1.0.3"]
            [lein-cljsbuild "1.1.5"]
            [lein-immutant "2.1.0"]
            [lein-with-env-vars "0.1.0"]
            [lein-garden "0.3.0"]]
  :env-vars {:LD_LIBRARY_PATH "/home/torysa/mkl/"}
  :garden {:builds [{:id "lineal"
                     :source-path "src/clj"
                     :stylesheet lineal.style/lineal
                     :compiler {:output-to "resources/public/css/style.css"
                                :pretty-print? true}}]}
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-to "target/cljsbuild/public/js/app.js"
                 :optimizations :advanced
                 :pretty-print false
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}
             
             
             :aot :all
             :uberjar-name "lineal.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "2021-04-23"]
                                 [ring/ring-mock "0.4.0"]
                                 [ring/ring-devel "1.13.0"]
                                 [pjstadig/humane-test-output "0.11.0"]
                                 [binaryage/devtools "1.0.7"]
                                 [com.cemerick/piggieback "0.2.2"]
                                 [doo "0.1.11"]
                                 [figwheel-sidecar "0.5.20"]
                                 [org.clojure/test.check "1.1.1"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                 [lein-doo "0.1.8"]
                                 [lein-figwheel "0.5.14"]
                                 [org.clojure/clojurescript "1.9.908"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "lineal.core/mount-components"}
                     :compiler
                     {:main "lineal.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true}}}}
                  
                  
                  
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "lineal.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}
                  
                  }
   :profiles/dev {}
   :profiles/test {}})
