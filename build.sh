#!/bin/bash
# Build script for SpiceDB IntelliJ Plugin

set -e

# Download SDK if not present
if [ ! -d ".intellij-sdk/lib" ]; then
    echo "Downloading IntelliJ SDK (first time only)..."
    mvn initialize -q
fi

# Build plugin
mvn clean package -q

echo "Build complete: target/intellij-spicedb-plugin-1.0.0-SNAPSHOT.zip"
