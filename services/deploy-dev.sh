#!/usr/bin/env bash

cd "${BASH_SOURCE%/*}" || exit

docker build -t ida .. -f backend-server/Dockerfile.dev
docker run -p 3000:8090 ida