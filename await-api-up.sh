#!/usr/bin/env bash

until $(curl --output /dev/null --silent --fail http://localhost:8080/api/v1/hubs); do
    sleep 1
done
