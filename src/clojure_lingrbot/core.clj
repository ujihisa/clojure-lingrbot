(ns clojure-lingrbot.core
  (:use
    [clojure.data.json :only (read-json)]
    [compojure.core]
    [ring.adapter.jetty]
    [clojail.core :only (sandbox)])
  (:import java.util.concurrent.ExecutionException))

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
              :let [expr (try (read-string code) (catch RuntimeException e '()))]]
          (when (list? expr)
            (try
              (format-for-lingr (sb (list 'let ['message message] expr)))
              (catch ExecutionException e nil))))]
          (clojure.string/join "\n" results))))

(defn -main []
  (run-jetty hello {:port 4001}))
