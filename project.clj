(defproject todo "0.1.0-SNAPSHOT"
  :description "Everyone's favourite todo app"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [compojure "1.5.0"]
                 [duct "0.5.10"]
                 [environ "1.0.2"]
                 [meta-merge "0.1.1"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring-jetty-component "0.3.1"]
                 [ring-webjars "0.1.1"]
                 [org.slf4j/slf4j-nop "1.7.14"]
                 [org.webjars/normalize.css "3.0.2"]
                 [com.datomic/datomic-pro "0.9.5350"]
                 [org.postgresql/postgresql "9.4.1208"]
                 [com.taoensso/timbre "4.3.1"]
                 [potemkin "0.4.3"]
                 [io.rkn/conformity "0.4.0"]
                 [metosin/ring-http-response "0.6.5"]]
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :plugins [[lein-environ "1.0.2"]
            [lein-gen "0.2.2"]]
  :generators [[duct/generators "0.5.10"]]
  :duct {:ns-prefix todo}
  :main ^:skip-aot todo.main
  :target-path "target/%s/"
  :aliases {"gen"   ["generate"]
            "setup" ["do" ["generate" "locals"]]}
  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:dependencies [[reloaded.repl "0.2.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [eftest "0.1.1"]
                                  [kerodon "0.7.0"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :env {:port "3000"
                         :transactor-uri "datomic:mem://todododo"}}
   :project/test  {:env {:transactor-uri "datomic:mem://todododo"}
                   :dependencies [[ring/ring-mock "0.3.0"]]}})
