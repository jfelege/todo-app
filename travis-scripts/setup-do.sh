#!/bin/bash

# install ruby gems v3 on host
gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
curl -sSL https://get.rvm.io | bash -s stable --ruby
source /usr/local/rvm/scripts/rv

# install travis ci cli for encrypting id_rsa
gem install travis -V

# install openjdk8 jdk package for springboot
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install openjdk-8-jdk

# run default docker containers
sudo docker run  --net="host" --restart=always -p 80:80 --name nginx --volume /host_docker-nginx/nginx.conf:/etc/nginx/nginx.conf:ro  --volume /host_todo-app/build/asciidoc/html5:/etc/nginx/app/apidocs -d nginx
sudo docker run  --restart=always -p 9411:9411 --name zipkin -d openzipkin/zipkin
