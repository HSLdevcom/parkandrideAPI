#!/usr/bin/env bash
set -eux

mvn clean verify -P noui --batch-mode --errors
