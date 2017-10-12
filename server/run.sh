#!/bin/bash

set -e

rm bin/*

cd src

javac Main.java -d ../bin
cd ../bin
java Main
