#!/bin/bash

DO_HOST=159.203.96.110

ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_todo-app/build/asciidoc'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_todo-app/build/libs'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_docker-nginx'

scp -oStrictHostKeyChecking=no -r /host_docker-nginx root@${DO_HOST}:/host_docker-nginx
scp -oStrictHostKeyChecking=no -r /host_todo-app/build/asciidoc root@${DO_HOST}:/host_todo-app/build/asciidoc
scp -oStrictHostKeyChecking=no -r /host_todo-app/build/libs root@${DO_HOST}:/host_todo-app/build/libs

