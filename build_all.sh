#!/bin/sh
./gradlew core:shadowJar backend-rpc-core:jar rewind_v1_5:jar eaglermotd:jar eaglerweb:jar plan:jar supervisor-core:jar
cp "core/build/libs/EaglerXServer.jar" "EaglerXServer.jar"
cp "backend-rpc-core/build/libs/EaglerXBackendRPC.jar" "EaglerXBackendRPC.jar"
cp "rewind_v1_5/build/libs/EaglerXRewind.jar" "EaglerXRewind.jar"
cp "eaglermotd/build/libs/EaglerMOTD.jar" "EaglerMOTD.jar"
cp "eaglerweb/build/libs/EaglerWeb.jar" "EaglerWeb.jar"
cp "plan/build/libs/EaglerXPlan.jar" "EaglerXPlan.jar"
cp "supervisor-core/build/libs/EaglerXSupervisor.jar" "EaglerXSupervisor.jar"
