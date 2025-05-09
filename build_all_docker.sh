#!/usr/bin/env bash

set -e

SCRIPT_DIR=$(dirname "$0")
cd "$SCRIPT_DIR"

if [ "$DOCKER_CMD" = "" ]; then
	DOCKER_CMD="docker"
fi

$DOCKER_CMD build -t ghcr.io/lax1dude/eaglerxserver:latest .
mkdir -p ./build
$DOCKER_CMD run -v ./build:/data/jars ghcr.io/lax1dude/eaglerxserver

echo "Success! Jars were copied to $( pwd )/build"

# You can run the below command to delete the container image from your system:
# docker image rm ghcr.io/lax1dude/eaglerxserver:latest --force
