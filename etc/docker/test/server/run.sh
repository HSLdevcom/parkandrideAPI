#!/bin/bash

docker run -v `pwd`:/data/parkandride/ -d -P 8080:8080 parkandrideapi/server