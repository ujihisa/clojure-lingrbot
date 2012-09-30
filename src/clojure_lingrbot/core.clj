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
        (let [message (:message (first (:events (read-json (slurp body)))))
              code (:text message)
              expr (try (read-string code) (catch RuntimeException e '()))]
            (if (list? expr)
              (try
                (format-for-lingr (sb (list 'let ['message message] expr)))
                (catch java.util.concurrent.ExecutionException e ""))
              ""))))

(defn -main []
  (run-jetty hello {:port 4001}))
