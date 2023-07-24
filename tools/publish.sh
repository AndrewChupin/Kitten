#!/bin/bash

# https://s01.oss.sonatype.org/#welcome
./gradlew clean api:assemble core:assemble android:assembleRelease
#./gradlew publishReleasePublicationToMavenLocal
./gradlew publishReleasePublicationToSonatypeRepository
