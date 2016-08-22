#!/bin/bash

echo "start up script"

# force gradle daemon to always run
[[ -d ~/.gradle ]] || mkdir ~/.gradle
touch ~/.gradle/gradle.properties && echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties

# switch cwd to todo-app folder
cd /host_todo-app

./gradlew assemble

# execute the application
nohup java -Dapp.pid.name=todoapp -Dspring.profiles.active=production -jar ./build/libs/*.jar 0<&- &>/dev/null &
