#!/bin/bash

if [ -r $1 ]; then
  mvn package -DskipTests && rm -Rf fakefed/ && ./urbit -l -F fed -B $1 -c fakefed
else
  echo "pill file does not exist"
fi
