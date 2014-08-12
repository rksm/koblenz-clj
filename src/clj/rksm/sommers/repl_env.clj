(ns rksm.sommers.repl-env
  (:require [rksm.subprocess :as p]
            [rksm.sommers.repl-server :as repl-server]
            [rksm.sommers.web-server :as web-server]
            [rksm.sommers.phantom :as phantom]))

(defonce shutdown-fn (atom nil))

(defonce nrepl-env (atom nil))

(defn boot-cljs []
  (web-server/start-server 8092)
  (let [repl-server-env (repl-server/start :cljs-connect true)
        phantom-proc (phantom/start-phantom-js "http://localhost:8092/example.html")]
    (reset! nrepl-env repl-server-env)
    (reset! shutdown-fn (fn []
                          (.destroy (:proc @phantom-proc))
                          (repl-server/stop repl-server-env)
                          (web-server/stop-server)))))

(defn shutdown []
  (@shutdown-fn))

(defn eval [form]
  (when (nil? @nrepl-env) (throw (Exception. "No nrepl environment")))
  (repl-server/eval @nrepl-env form))

;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

(comment
  (defn- start [repl-state]
    )

  (defprotocol ReplTester
    (start [this])
    (stop [this])
    (eval [this form]))

  (deftype PhantomReplTester [repl-state]
    ReplTester
    (start [this] (start repl-state))
    (stop [this] (stop repl-state))
    (eval [this form] (eval repl-state form)))


  (defn create-repl
    []
    (when-not (repl-server/running?) (repl-server/start))
    (phantom/start-phantom-js "http://localhost:3000/example.html")))

(comment

  (server/stop-server server)
  (->> (ns-map *ns*))
  (ns-interns *ns*)
  (ns-publics *ns*)
  (refresh)
  (doseq [sym ['env 'start-server 'conn 'session]] (ns-unmap *ns* sym))

  (.* server)

  ;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  (js/alert 2)
  (running?)
  (stop)
  (start)
  (eval '(.-userAgent js/navigator))
  (eval '(+ 1 4))
  (eval '(js/alert 2))

  ;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  (lein cljsbuild once)

  (require '[rksm.sommers.proc :as p])
  (def proc (p/start-phantom-js "http://localhost:3000/example.html"))
  (.destroy (:proc proc))
  )
