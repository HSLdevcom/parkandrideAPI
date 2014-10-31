#!/bin/bash

function run() {
  # assume workdir is repo root
  local frontend_dir="application/src/main/frontend"
  rm -rf $frontend_dir/bin
  rm -rf $frontend_dir/build
  rm -rf $frontend_dir/vendor
  rm -rf $frontend_dir/vendor
  rm -rf $frontend_dir/node_modules
}

VERBOSE="true"

source $(dirname $0)/../main.inc