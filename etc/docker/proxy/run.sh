#!/bin/bash

docker run -p 9090:8080 -v `pwd`:/data/ --name nproxy dockerfile/java:oracle-java8 java -jar /data/proxy-0.0.1-df7dab3.jar