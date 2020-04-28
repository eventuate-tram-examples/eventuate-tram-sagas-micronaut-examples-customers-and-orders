#! /bin/bash

set -e


dockerall="./gradlew ${DATABASE?}Compose"
dockercdc="./gradlew ${DATABASE?}cdcCompose"

./gradlew testClasses

${dockerall}Down
${dockercdc}Build
${dockercdc}Up

./gradlew -x :end-to-end-tests:test build

#Testing db cli
if [ "${DATABASE}" == "mysql" ]; then
  echo 'show databases;' | ./mysql-cli.sh -i
elif [ "${DATABASE}" == "postgres" ]; then
  echo '\l' | ./postgres-cli.sh -i
else
  echo "Unknown Database"
  exit 99
fi

${dockerall}Build
${dockerall}Up

./gradlew :end-to-end-tests:cleanTest :end-to-end-tests:test

${dockerall}Down
