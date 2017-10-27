#!/bin/bash

rm -rf ./bin > /dev/null 2>&1
mkdir ./bin

set -e

cd src

javac -cp .:../lib/* Main.java -d ../bin
cd ../bin
clear
java -cp .:../lib/* Main $@
