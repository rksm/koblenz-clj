(ns rksm.koblenz.nrepl-server-test
  (:require [clojure.test :refer :all]
            [rksm.koblenz.repl-server :as s]
            [rksm.koblenz.phantom :as phan]
            [clojure.core.async :as async]
            [rksm.koblenz.web-server :as web]))


(def test-env (atom s/default-env))

(deftest repl-server-clj
  (testing "Server start and stop without cljs connection"
    (s/start-with-env test-env :cljs-connect false)
    (is (s/running? test-env))
    (let [eval-res (s/eval test-env '(+ 20 3))
          answer (:value (first eval-res))]
      (is (= "23" answer)))
    (s/stop test-env)
    (is (not (s/running? test-env)))))

(deftest repl-server-cljs
  (testing "Server start and stop without cljs connection"
    (s/start-with-env test-env :cljs-connect true)
    (is (s/running? test-env))
    (web/start-server 8090)
    (let [phantom-proc (phan/start-phantom-js "http://localhost:8090/example.html")]
     (let [eval-res (s/eval test-env '(+ 20 3))
           answer (:value (first eval-res))]
       (is (= "23" answer)))
     (.destroy (:proc @phantom-proc)))
    (s/stop test-env)
    (is (not (s/running? test-env)))
    (web/stop-server)))

;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

(comment

  (run-tests *ns*)
  (test-var #'foo.core-test/repl-env-cljs)
  (test-var #'repl-server-cljs)

  ;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  (require '[clojure.string :as string])
  (sh "/bin/bash" "--login" "-c" "'lsof -i tcp:7883'")
  (string/trim (:out (sh "lsof" "-t" "-i" "tcp:7888")))
  (sh "kill" "27461")
  (def v (s/start))
  (def v (s/start :cljs-connect false))
  (def v test-env)
  (def v repl-env/nrepl-env)
  (s/eval v '(+ 20 3))
  (s/stop v)

  (.* (:server-socket (:server v)))
  (.isClosed (:server-socket (:server @v)))
  (.close (:server-socket (:server @v)))
  (.close (:conn @v))
  (.isBound (:server-socket (:server @v)))
  (.* (:server-socket (:server @v)))

  (def v2  (atom v))
  (require '[clojure.tools.nrepl.server :as server])
  (server/stop-server (:server v))
  (s/eval v '(+ 1 2))
  )
