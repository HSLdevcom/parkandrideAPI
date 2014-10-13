#!/bin/bash

docker run -d --privileged -v /var/run/docker.sock:/var/run/docker.sock --link gocd-server:goserver --name gocd-agent parkandrideapi/gocd-agent