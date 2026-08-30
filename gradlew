#!/usr/bin/env sh
# Gradle wrapper stub - CI uses gradle directly, local uses this shim
# If gradle-wrapper.jar exists, use it, otherwise fallback to system gradle
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
  exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
else
  exec gradle "$@"
fi
