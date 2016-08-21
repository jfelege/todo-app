#!/bin/bash

echo ${TRAVIS_BUILD_DIR}

ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@159.203.96.110 'mkdir -p /host_todo-app/build/asciidoc'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@159.203.96.110 'mkdir -p /host_todo-app/build/libs'