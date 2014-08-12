(ns rksm.sommers.core
  (:require [clojure.browser.repl :as default-repl]
            [weasel.repl :as weasel-repl]
            [rksm.sommers.config :refer [config]]))

(if (:nrepl-websocket? config)
  (if-not (weasel-repl/alive?)
    (weasel-repl/connect
     (str "ws://localhost:" (:nrepl-tcp-port config))
     :verbose true))
  (default-repl/connect
    (str "http://localhost:" (:nrepl-websocket-port config) "/repl")))
