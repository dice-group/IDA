#!/usr/bin/env bash

cd "${BASH_SOURCE%/*}" || exit

### if logs folder does not exist then create it
[ ! -d ~/ida-prod-logs ] && mkdir -p ~/ida-prod-logs

version=$(cat VERSION)
registry=${REGISTRY:-localhost:5000}

fuseki_pw=${FUSEKI_PW:-$(cat FUSEKI_PW)}

function build_container() {
    tag=$registry/ida/$1:$version
    echo "Building $1: $tag..."
    if docker build "${@:2}" -t "$tag"; then
        echo "Successfully built $tag."
        if docker push "$tag"; then
            echo "Successfully pushed $tag."
        fi
    fi
}

build_container nginx .. -f frontend/Dockerfile.prod
build_container backend-server .. -f backend-server/Dockerfile.prod
build_container fuseki-server .. -f Dockerfile.fuseki

export REGISTRY=$registry
export VERSION=$version
export FUSEKI_PW=$fuseki_pw

docker stack deploy --compose-file docker-compose-prod.yml ida-stack-prod
