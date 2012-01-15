(ns lingr-clojure-bot.core
  (:use
    [clojure.data.json :only (read-json)]
    [compojure.core]
    [ring.adapter.jetty]
    [clojail core])
  (:import java.util.concurrent.ExecutionException))

(def sb (sandbox #{}))
(sb '(do
       (defn what-time []
         (.format (doto
                    (java.text.DateFormat/getDateTimeInstance)
                    (.setTimeZone (java.util.TimeZone/getTimeZone "PST")))
                  (java.util.Date.)))
       (defn sun []
         "http://factoryjoe.s3.amazonaws.com/emoticons/emoticon-0157-sun.gif")
       (defn rock []
         "http://factoryjoe.s3.amazonaws.com/emoticons/emoticon-0178-rock.gif")
       (defn locals [] (ns-interns *ns*))))

(defn format-for-lingr [obj]
  (str (cond (seq? obj) (seq obj)
             :else obj)))

(defroutes
  hello
  (GET "/" [] "hello")
  (POST "/"
        {body :body}
        (let [message (:message (first (:events (read-json (slurp body)))))]
          (sb (list 'def 'message message))
          (let [code (:text message)
                expr (try (read-string code) (catch java.lang.RuntimeException e '()))]
            (if (list? expr)
              (try
                (format-for-lingr (sb expr))
                (catch java.util.concurrent.ExecutionException e ""))
              "")))))

(defn -main []
  (run-jetty hello {:port 80}))
