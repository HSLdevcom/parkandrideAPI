#!/bin/bash

docker run -d --privileged --link gocd-server:goserver --name gocd-agentX parkandrideapi/gocd-agent