(ns rksm.sommers.repl-env-test
  (:require [clojure.test :refer :all]
            [rksm.sommers.repl-server :as s]
            [clojure.core.async :as async]
            [rksm.sommers.repl-env :as repl-env]))

(use-fixtures :once
  (fn [f]
    (repl-env/boot-cljs)
    (f)
    (repl-env/shutdown)))

(deftest repl-env-cljs
  (testing "simple eval"
    (let [eval-res (repl-env/remote-eval (+ 20 3))
          answer (:value (first eval-res))]
      (is (= "23" answer)))
    (is (= 23 (repl-env/remote-eval-val (+ 20 3))))))

;; -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

(comment

  (run-tests *ns*)

  (do
    (repl-env/boot-cljs)
    (while (not (s/running? @repl-env/nrepl-env))
      (println "not running") (Thread/sleep 40))
    (repl-env/eval '(+ 20 3)))

  (repl-env/eval '(+ 20 3))
  (repl-env/eval '(.log js/console js/Array))

  (require '[rksm.sommers.config :as c])
  c/config
  
  (repl-env/boot-cljs)
  (repl-env/shutdown)

  (lein cljsbuild clean)
  (lein cljx once)
  (lein cljsbuild once)

  (refresh)
  
    (async/<!! (:out proc))
  )
