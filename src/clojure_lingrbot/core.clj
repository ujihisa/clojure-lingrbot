(ns clojure-lingrbot.core
  (:require ; just for exposing them to let users use
            [clojure.core.match]
            [clojure.core.strint]
            [clj-time.coerce]
            [clj-time.core]
            [incanter.core]
            ; Actual requires
            [clojure.data.json :refer [read-json]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojail.core :refer [sandbox]]
            [compojure.core :refer :all])
  (:import [java.util.concurrent ExecutionException])
  (:gen-class))

(def sb (sandbox #{}))
(sb '(do
       (defn what-time
         ([] (what-time 'PST))
         ([tz] (.format (doto
                          (java.text.DateFormat/getDateTimeInstance
                            java.text.DateFormat/FULL
                            java.text.DateFormat/FULL)
                          (.setTimeZone (java.util.TimeZone/getTimeZone (str tz))))
                        (java.util.Date.))))
       (defn sun []
         "http://factoryjoe.s3.amazonaws.com/emoticons/emoticon-0157-sun.gif")
       (defn rock []
         "http://factoryjoe.s3.amazonaws.com/emoticons/emoticon-0178-rock.gif")
       (defn locals [] (ns-interns *ns*))))

(defn format-for-lingr [obj]
  (cond
    (seq? obj) (str (seq obj))
    :else (str obj)))

(defroutes hello
  (GET "/" []
    (str {:author "Tatsuhiro Ujihisa"
          :web "https://github.com/ujihisa/clojure-lingrbot"}))
  (POST "/" {body :body}
    (let [results
          (for [message (map :message (:events (read-json (slurp body))))
                :let [code (:text message)]
                :let [expr (try
                             (read-string code)
                             (catch RuntimeException e nil))]]
            (when (and (sequential? expr)
                       (not (string? expr)))
              (try
                (format-for-lingr (sb (list 'let ['message message] expr)))
                (catch ExecutionException e nil))))]
      (clojure.string/join "\n" results))))

(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "4001"))]
    (run-jetty hello {:port port})))
; vim: set lispwords+=GET,POST :
