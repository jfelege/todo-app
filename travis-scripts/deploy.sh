#!/bin/bash

ssh -oStrictHostKeyChecking=no -i ./travis-scripts/id_rsa root@159.203.96.110 'mkdir -p /host_todo-app/build/asciidoc'
ssh -oStrictHostKeyChecking=no -i ./travis-scripts/id_rsa root@159.203.96.110 'mkdir -p /host_todo-app/build/libs'