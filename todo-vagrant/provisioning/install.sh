#/bin/bash

echo "update vmhost with latest packages"
sudo yum update -y

echo "install openjdk-1.8.0"
sudo yum install -y java-1.8.0-openjdk-devel

# force gradle daemon to always run
[[ -d ~/.gradle ]] || mkdir ~/.gradle
touch ~/.gradle/gradle.properties && echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties