#!/usr/bin/env bash

$JAVA_HOME/bin/java -Xms8G -Xmx8G  -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:MaxDirectMemorySize=40g -Dlog.home=/var/log/buck-cache-client/logs -jar cache/build/libs/cache-1.0.0-standalone.jar server cache/src/dist/config/sjc1.yml
