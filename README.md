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
3. run `vagrant up` (this step may take a few minutes)
4. point browser to [http://localhost:8080/api/auth/token?username=jdoe&password=password/](http://localhost:8080/api/auth/token?username=jdoe&password=password) to fetch a `ROLE_USER` jwt token.
5. point browser to [http://localhost:8080/api/auth/token?username=jfelege&password=password/](http://localhost:8080/api/auth/token?username=jfelege&password=password) to fetch a `ROLE_USER`, `ROLE_ADMIN` jwt token.
6. point browser to [http://localhost:8080/api/hello/?token=**TOKEN**](http://localhost:8080/api/hello/?token=**TOKEN**) to access normal user end point.
7. point browser to [http://localhost:8080/api/hello/admin/?token=**TOKEN**](http://localhost:8080/api/hello/admin/?token=**TOKEN**) to access normal admin end point.