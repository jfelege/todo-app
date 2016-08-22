#!/bin/bash

DO_HOST=159.203.96.110

echo "TRAVIS_BUILD_DIR => ${TRAVIS_BUILD_DIR}"

echo "MKDIR on remote"
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_todo-app/build/asciidoc'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_todo-app/build/libs'
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'mkdir -p /host_docker-nginx'

echo "UPLOAD nginx config"
scp -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa -r ${TRAVIS_BUILD_DIR}/todo-vagrant/docker-nginx/nginx.conf root@${DO_HOST}:/host_docker-nginx/nginx.conf

echo "UPLOAD asciidoc"
scp -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa -r ${TRAVIS_BUILD_DIR}/todo-app/build/asciidoc root@${DO_HOST}:/host_todo-app/build

echo "UPLOAD libs"
scp -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa -r ${TRAVIS_BUILD_DIR}/todo-app/build/libs root@${DO_HOST}:/host_todo-app/build

echo "KILL springboot process"
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'pkill PIDNAME=todoapp'

echo "START springboot process"
ssh -oStrictHostKeyChecking=no -i ${TRAVIS_BUILD_DIR}/travis-scripts/id_rsa root@${DO_HOST} 'nohup java PIDNAME=todoapp -Dspring.profiles.active=production -jar /host_todo-app/build/libs/*.jar 0<&- &>/dev/null &'