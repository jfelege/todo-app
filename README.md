# todo-app

This project contains an example implementation of a REST API service to manage tasks and todo lists using SpringBoot, Vagrant.

## pre-req

The following software packages are required:

* OpenJDK 1.8
* Vagrant 1.8.4
* VirtualBox 5.0.22

## getting started

1. clone this git repo locally
2. navigate to `./todo-vagrant`
3. run `vagrant up` 
4. point browser to [http://localhost:6080](http://localhost:6080) for api documentation (this may take a few minutes)
5. point browser to [http://localhost:9411/](http://localhost:9411/) for OpenZipkin UI.

### vagrant
The included vagrant environment includes the installation of docker-engine configured to run a instance of nginx (web server) acting as a reverse proxy to the spring boot api application.

It is also running OpenZipkin to demonstrate an initial capability for tracing distributed events.

When the vagrant image is provisioned, the java application is re-built which may take a minute or two before coming online. 

Spring Boot will use the `production` profile while running in vagrant.

### demo accounts

* admin:password
* activeuser:password
* activeuser2:password

## other notes
* application dependencies:
    * spring boot
    * spring-data-jpa, hsql in-memory database
    * spring-security
    * spring-cloud-starter-zipkin
    * spring-security-test
    * spring-rest-docs
    * jBCrypt (for encrypting account passwords)
    * java-jwt (for token management)
