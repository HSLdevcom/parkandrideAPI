#!/bin/bash

docker run -d --link gocd-server:goserver --name gocd-agentX parkandrideapi/gocd-agent