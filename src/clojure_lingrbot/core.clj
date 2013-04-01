(ns clojure-lingrbot.core
  (:use [compojure.core]
        [clojure.data.json :only (read-json)]
        [ring.adapter.jetty :only (run-jetty)]
        [clojail.core :only (sandbox)])
  (:import java.util.concurrent.ExecutionException)
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
  (GET "/" [] "hello")
  (POST "/"
        {body :body}
        (let [results (for [message (map :message (:events (read-json (slurp body))))
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

(defn -main [& args]
  (let [port (or (first args) 4001)]
    (run-jetty hello {:port port})))
