#/bin/bash

echo "update vmhost with latest packages"
sudo yum update -y

echo "install openjdk-1.8.0"
sudo yum install -y java-1.8.0-openjdk-devel

# install docker engine
sudo tee /etc/yum.repos.d/docker.repo <<-'EOF'
[dockerrepo]
name=Docker Repository
baseurl=https://yum.dockerproject.org/repo/main/centos/7/
enabled=1
gpgcheck=1
gpgkey=https://yum.dockerproject.org/gpg
EOF

sudo yum install -y docker-engine

sudo service docker start

# test docker installation is working
sudo docker run hello-world

# docker engine to start on boot
sudo chkconfig docker on

sudo docker run --net="host" --restart=always -p 80:80 --name nginx --volume /host_docker-nginx/nginx.conf:/etc/nginx/nginx.conf:ro  --volume /host_todo-app/build/asciidoc/html5:/etc/nginx/app/apidocs -d nginx

# sudo docker run -it nginx /bin/bash