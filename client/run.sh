#!/bin/bash

rm -rf /home/ths/talkp/client/bin/ > /dev/null 2>&1
mkdir /home/ths/talkp/client/bin

set -e

cd src

javac -cp .:../lib/* Main.java -d ../bin
cd ../bin
clear
java -cp .:../lib/* Main $@
