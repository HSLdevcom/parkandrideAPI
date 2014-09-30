#!/bin/bash

docker run -d -p 8080:8080 -v `pwd`:/data/parkandride/ --name parkandrideapi-server parkandrideapi/server