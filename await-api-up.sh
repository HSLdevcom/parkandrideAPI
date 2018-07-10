#!/usr/bin/env bash
set -u

tries=0
max_tries=20
interval=1

while true; do
    output=$(curl --max-time 5 --silent --verbose --fail http://localhost:8080/api/v1/hubs 2>&1)
    status=$?
    if [[ "$status" -eq 0 ]]; then
        exit 0
    fi
    ((tries++))
    if [[ "$tries" -ge "$max_tries" ]]; then
        echo "$output"
        exit 1
    fi
    sleep "$interval"
done
