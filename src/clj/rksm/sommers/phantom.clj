(ns rksm.sommers.phantom
  (:require [rksm.subprocess :as p]
            [clojure.core.async :as async]))

(def phantomjs-source
  (let [f (doto (java.io.File/createTempFile "phantomjs_repl" ".js")
            .deleteOnExit
            (spit (str "var page = require('webpage').create();"
                       "var url = require('system').args[1]; console.log(url);"
                       "var system = require('system');"
                       "page.onConsoleMessage = function(msg) { system.stdout.writeLine('phantom@' + url + ': ' + msg); };"
                       "page.open(url, function(status) { console.log('loading ' + status); });")))]
    (.getAbsolutePath f)))

(defn start-phantom-js
  [url]
  (let [p (p/async-proc "phantomjs" phantomjs-source url)]
    (println "phantom started")
    (future (while (not (.closed? (:out @p)))
              (println (async/<!! (:out @p)))))
    p))


(comment
  (def proc (start-phantom-js "http://localhost:8092/example.html"))
  (future (Thread/sleep 5000) (.destroy (:proc proc)))
  (.destroy (:proc proc))
  (.exitValue (:proc proc))
  (async/take! (:out proc) println)
  (async/<!! (:out proc))
  (.closed? (:out proc))

  )
