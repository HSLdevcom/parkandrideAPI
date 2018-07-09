#!/usr/bin/env bash
set -eux

mvn clean verify --batch-mode --errors
