(defproject clojure-lingrbot "1.1.2-SNAPSHOT"
  :description "Clojure REPL for Lingr"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [compojure "1.0.0-RC2"]
                 [ring "1.0.1"]
                 [clojail "1.0.6"]
                 [org.clojure/core.incubator "0.1.3"]
                 [org.clojure/core.match "0.2.0"]]
  :aot :all
  :main clojure-lingrbot.core
  :jvm-opts ["-Djava.security.policy=example.policy"])
