#!/usr/bin/env bash
clear
./gradlew :library:clean :library:assembleRelease :library:generatePomFileForReleasePublication :library:publishReleasePublicationToMavenLocal