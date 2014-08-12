#!/usr/bin/env bash

# currently not working:
#   lein with-profiles +cljs-weasel-repl repl :headless

LEIN=`which lein2`
export LEIN=${LEIN:=lein}

$LEIN repl :headless
