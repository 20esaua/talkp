# powershell server start script.
# expects you to have javac and java in your path.
rm bin/*
cd src
javac -cp "..\lib\*;." Main.java -d ..\bin
cd ..\bin
java -cp "..\lib\*;." Main
cd ..