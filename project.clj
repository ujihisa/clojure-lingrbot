(defproject clojure-lingrbot "1.1.0-SNAPSHOT"
  :description "Clojure REPL for Lingr"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.1.2"]
                 [compojure "1.0.0-RC2"]
                 [ring "1.0.1"]
                 [clojail "1.0.6"]
                 [org.jruby/jruby-complete "1.7.3"]]
  :main clojure-lingrbot.core
  :jvm-opts ["-Djava.security.policy=example.policy"])
