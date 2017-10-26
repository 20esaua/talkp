#!/bin/bash

rm -rf /home/ths/talkp/server/bin > /dev/null 2>&1
mkdir /home/ths/talkp/server/bin

set -e

cd src

javac -cp .:../lib/* Main.java -d ../bin
cd ../bin
clear
java -cp .:../lib/* Main $@
