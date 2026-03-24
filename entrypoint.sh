#!/bin/bash

mkdir -p /destination
javac -cp lib/gson-2.8.9.jar:lib/vlcj-3.12.1.jar:lib/jna-4.5.2.jar:lib/jna-platform-4.5.2.jar:lib/slf4j-api-1.7.30.jar:src -d class src/P2P.java
exec java -cp lib/gson-2.8.9.jar:lib/vlcj-3.12.1.jar:lib/jna-4.5.2.jar:lib/jna-platform-4.5.2.jar:lib/slf4j-api-1.7.30.jar:lib/slf4j-simple-1.7.30.jar:class P2P
