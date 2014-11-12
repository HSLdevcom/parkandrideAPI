#!/bin/bash

docker run --name nexus-vol parkandrideapi/nexus echo "create vol done"
docker run -d -p 8081:8081 --volumes-from nexus-vol --name nexus parkandrideapi/nexus