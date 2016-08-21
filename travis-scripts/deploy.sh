#!/bin/bash

DO_HOST=159.203.96.110

echo "TRAVIS_BUILD_DIR => ${TRAVIS_BUILD_DIR}"

echo "MKDIR on remote"
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_todo-app/build/asciidoc'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_todo-app/build/libs'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_docker-nginx'

echo "FOLDER"
ls .

ls ${TRAVIS_BUILD_DIR}

ls ${TRAVIS_BUILD_DIR}/todo-vagrant

ls ${TRAVIS_BUILD_DIR}/todo-app

echo "UPLOAD nginx config"
scp -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa -r root@${DO_HOST}:/host_docker-nginx ${TRAVIS_BUILD_DIR}/todo-vagrant/docker-nginx/

echo "UPLOAD asciidoc"
scp -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa -r root@${DO_HOST}:/host_todo-app/build/asciidoc ${TRAVIS_BUILD_DIR}/todo-app/build/

echo "UPLOAD libs"
scp -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa -r root@${DO_HOST}:/host_todo-app/build/libs ${TRAVIS_BUILD_DIR}/todo-app/build/

