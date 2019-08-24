#!/usr/bin/env bash
mvn release:perform -DuseReleaseProfile=false -Darguments="-DskipTests" -Prelease
