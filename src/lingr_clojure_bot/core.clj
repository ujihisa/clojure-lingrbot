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
        (let [code (:text (:message (first (:events (read-json (slurp body))))))
              expr (read-string code)]
          (if (list? expr)
            (try
              (str (sb expr))
              (catch java.util.concurrent.ExecutionException e ""))
            ""))))

(defn -main []
  (run-jetty hello {:port 80}))
