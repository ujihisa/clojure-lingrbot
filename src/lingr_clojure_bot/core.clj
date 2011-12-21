(ns lingr-clojure-bot.core
  (:use
    [clojure.data.json :only (read-json)]
    [compojure.core]
    [ring.adapter.jetty]
    [clojail core])
  (:import java.util.concurrent.ExecutionException))

(def sb (sandbox #{}))

(defroutes
  hello
  (GET "/" [] "hello")
  (POST "/"
        {body :body}
        (str (try
               (let [code (:text (:message (first (:events (read-json (slurp body))))))]
                 (sb (read-string code)))
               (catch java.util.concurrent.ExecutionException e (str e))))))

(defn -main []
  (run-jetty hello {:port 80}))
