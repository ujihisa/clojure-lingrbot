(defproject clojure-lingrbot "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.1.2"]
                 [compojure "1.0.0-RC2"]
                 [ring "1.0.1"]
                 [clojail "0.5.1"]]
  :main clojure-lingrbot.core
  :jvm-opts ["-Djava.security.policy=example.policy"]))
