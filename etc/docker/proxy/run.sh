#!/bin/bash

docker run -d -p 9090:8080 -v `pwd`:/data/ --name nproxy dockerfile/java:oracle-java8 java -jar /data/proxy-latest.jar