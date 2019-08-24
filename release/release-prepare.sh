#!/usr/bin/env bash
mvn release:prepare -Darguments="-DskipTests" -DautoVersionSubmodules
