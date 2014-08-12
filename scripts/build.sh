#!/usr/bin/env bash

LEIN=`which lein2`
export LEIN=${LEIN:=lein}

$LEIN do \
      cljx once, \
      cljsbuild clean, \
      cljsbuild once
