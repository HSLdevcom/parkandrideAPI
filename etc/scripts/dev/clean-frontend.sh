#!/bin/bash

function init() {
  ROOT_DIR=$SCRIPT_DIR/../../..

  FRONTEND_DIR="$ROOT_DIR/application/src/main/frontend"
  PTOR_DIR="$ROOT_DIR/etc/protractor"
}

function run() {
  for i in bin build vendor node node_modules; do
    rm -rf $FRONTEND_DIR/$i
  done

  rm -rf $PTOR_DIR/node_modules
}

VERBOSE="true"
source $(dirname $0)/../main.inc