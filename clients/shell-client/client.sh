#!/bin/sh
# Author: ginkoid

{ echo "{\"type\":0,\"online\":true,\"username\":\"$(whoami)\"}"; jq --unbuffered -cRr '{type:1,message:.}'; } | nc romania.arinerron.com 25678 | jq -r '"[" + .username + "] " + .message'
