#!/bin/bash

# always stop on errors
set -e

function fullpath() {
  local pth=$1
  case $pth in
    /*)
      ;;
    *)
      pth=$(pwd)/$pth
      ;;
  esac
  echo $pth
}

# isFunction(name)
# - to be used in an if, so return 0 if successful and 1 if not!
function isFunction {
  if [[ $(type -t $1) == "function" ]]; then
    return 0
  else
    return 1
  fi
}

function main {
  # resolve absolute script directory
  SCRIPT_DIR=$(fullpath $(dirname $0))

  # --verbose argument
  if [[ $VERBOSE == "true" ]]; then
    set -x
  fi

  if isFunction init; then
    init "$@"
  fi

  run "$@"
}

main "$@"
