#/bin/bash

echo "update vmhost with latest packages"
sudo yum update -y

echo "install openjdk-1.8.0"
sudo yum install -y java-1.8.0-openjdk-devel