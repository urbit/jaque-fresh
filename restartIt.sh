#!/bin/bash

if [ -r $1 ]; then
  mvn package -DskipTests && rm -Rf tmp-run/ && cp -r $1 tmp-run/ && ./urbit tmp-run/
else
  echo "that pier does not exist"
fi
