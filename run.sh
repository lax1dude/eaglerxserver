#!/usr/bin/env bash
# place in the same directory as Eaglercraft.jar
# Simple launcher:
cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -Xmx4G -Xms2G -jar Eaglercraft.jar nogui

# Detached logging variant (uncomment to use):
# nohup java -Xmx4G -Xms2G -jar Eaglercraft.jar nogui > server.log 2>&1 & echo $! > eaglercraft.pid