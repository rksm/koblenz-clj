#!/usr/bin/env bash

LEIN=`which lein2`
export LEIN=${LEIN:=lein}

$LEIN ring server-headless
