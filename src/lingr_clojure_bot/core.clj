(ns lingr-clojure-bot.core
  (:use
    [clojure.data.json :only (read-json)]
    [compojure.core]
    [ring.adapter.jetty]
    [clojail core])
  (:import java.util.concurrent.ExecutionException))

(def sb (sandbox #{}))
(defroutes hello
           (GET "/" [] "hello")
           (POST
             "/"
             {body :body}
             (str (try
                    (sb (read-string (:text (:message (read-json (slurp body))))))
                    (catch java.util.concurrent.ExecutionException e (str e))))))

(defn -main []
  (run-jetty hello {:port 4567}))
