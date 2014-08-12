(ns rksm.koblenz.repl-server
  (:require [clojure.tools.nrepl.server :as server]
            [clojure.tools.nrepl :as nrepl]
            [cider.nrepl]
            [cemerick.piggieback]
            [rksm.koblenz.config :refer [config]]))


(def default-env {:port 7892
                  :server nil
                  :conn nil
                  :session nil
                  :browser-repl-enabled false})

(defn- reset-env
  [env]
  (reset! env default-env))

(defn running?
  [env]
  (let [{server :server} @env]
    (not (or
          (nil? server)
          (.isClosed (:server-socket server))))))

(defn start-server
  [env]
  (when (running? env) (throw (Exception. "Server already running")))
  (let [{port :port} @env
        server (server/start-server
                :port port
                :handler (apply server/default-handler
                                #'cemerick.piggieback/wrap-cljs-repl
                                (map resolve cider.nrepl/cider-middleware)))
        conn (nrepl/connect :port port)
        session (nrepl/client-session (nrepl/client conn Long/MAX_VALUE))]
    (.setReuseAddress (:server-socket server) true)
    (swap! env #(assoc % :server server :conn conn :session session))))

(defn stop-server
  [env]
  (when-not (running? env) (throw (Exception. "Server not running")))
  (let [{server :server conn :conn} @env]
   (.close conn)
   (server/stop-server server)))

(defn eval
  [env form]
  (when-not (running? env) (throw (Exception. "Server not running")))
  (let [{session :session} @env
        result (doall (nrepl/message session {:op "eval" :code (str form)}))]
    result))

(defn start-browser-repl-weasel
  [env]
  (eval env '(do (require 'weasel.repl.websocket)
             (cemerick.piggieback/cljs-repl
              :repl-env (weasel.repl.websocket/repl-env
                         :ip "0.0.0.0"
                         :port (:nrepl-websocket-port config)))))
  (swap! env #(assoc % :browser-repl-enabled true)))

(defn start-browser-repl-default
  [env]
  (eval env '(do (require 'cljs.repl.browser)
                 (cemerick.piggieback/cljs-repl
                  :repl-env (cljs.repl.browser/repl-env
                             :port (:nrepl-tcp-port config)))))
  (swap! env #(assoc % :browser-repl-enabled true)))

(defn start-browser-repl
  [env]
  (if (:nrepl-websocket? config)
   (start-browser-repl-weasel env)
   (start-browser-repl-default env)))

(defn stop-browser-repl
  [env]
  (eval env :cljs/quit)
  (swap! env #(assoc % :browser-repl-enabled false)))


(defn stop
  [env]
  (try (stop-browser-repl env)
       (catch Exception e
         (println "Could not stop the browser repl: " e)))
  (stop-server env)
  @(future (loop []
             (Thread/sleep 100)
             (when-not (.isClosed (:server-socket (:server @env)))
               (recur))))
  env)

(defn start-with-env
  [env & {:keys [cljs-connect] :or {cljs-connect true} :as options}]
  (when (running? env) (stop env))
  (try
   (start-server env)
   (when cljs-connect (start-browser-repl env))
   (catch Exception err
     (do
       (try (stop-server env) (catch Exception _))
       (throw err))))
  env)

(defn start
  [& options]
  (apply start-with-env (atom default-env) options))


;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-


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
  
  (require '[rksm.koblenz.proc :as p])
  (def proc (p/start-phantom-js "http://localhost:3000/example.html"))
  (.destroy (:proc proc))

  ;; -=-=-=-=-=-=-=-
  ;; cider inspect
  ;; -=-=-=-=-=-=-=-

  ;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  ;; ops:
  ;; "inspect-reset"
  ;; "inspect-refresh"
  ;; "inspect-push"
  ;; "inspect-pop"
  ;; "inspect-start"

  ;; supported types:
  ;; (map? obj) :seq
  ;; (vector? obj) :seq
  ;; (seq? obj) :seq
  ;; (set? obj) :seq
  ;; (var? obj) :var
  ;; (string? obj) :string
  ;; (instance? Class obj) :class
  ;; (instance? clojure.lang.Namespace obj) :namespace
  ;; (instance? clojure.lang.ARef obj) :aref
  ;; (.isArray (class obj)) :array
  ;; :default (or (:inspector-tag (meta obj)) (type obj))
  
  )
