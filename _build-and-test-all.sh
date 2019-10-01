#! /bin/bash

set -e


. ./set-env-${DATABASE?}.sh

dockerall="./gradlew ${DATABASE?}Compose"
dockercdc="./gradlew ${DATABASE?}cdcCompose"

./gradlew testClasses

${dockerall}Down
${dockercdc}Build
${dockercdc}Up

./wait-for-services.sh $DOCKER_HOST_IP "8099" "actuator/health"

./gradlew -x :end-to-end-tests:test build

${dockerall}Build
${dockerall}Up

./wait-for-services.sh $DOCKER_HOST_IP "8081 8082" "health"

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

${dockerall}Down
