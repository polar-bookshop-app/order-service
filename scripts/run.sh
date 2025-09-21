#!/usr/bin/env bash

# JVM
JMX_REMOTE_CONNECTION="-Dcom.sun.management.jmxremote \
  -Djava.rmi.server.hostname=localhost"

REMOTE_DEBUGGER=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8001

export JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError \
  -XX:MaxDirectMemorySize=10M \
  -Xmx1506507K \
  -XX:MaxMetaspaceSize=78644K \
  -XX:ReservedCodeCacheSize=240M \
  -Xss1M \
  $JMX_REMOTE_CONNECTION \
  $REMOTE_DEBUGGER"

java -jar build/libs/order-service-0.0.1-SNAPSHOT.jar