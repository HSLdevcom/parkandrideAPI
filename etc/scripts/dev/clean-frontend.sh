#!/bin/bash

function init() {
  ROOT_DIR=$SCRIPT_DIR/../../..
}

function run() {
  local frontend_dir="$ROOT_DIR/application/src/main/frontend"
  rm -rf $frontend_dir/bin
  rm -rf $frontend_dir/build
  rm -rf $frontend_dir/vendor
  rm -rf $frontend_dir/node_modules
}

VERBOSE="true"

source $(dirname $0)/../main.inc