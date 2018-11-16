#!/bin/bash
:<<!
Author: lwenxu
Description: this script will build project by maven and build docker image then run the spring project with docker.
             With use the script you must pass two parameters , appName and Port . appName will be used to name docker image
             and the port number will serve as an open port for host computer.
!
NAME=$1
PORT=$2
PROFILE=$3
VERSION=1.2.1
APP_NAME=${NAME}-${PROFILE}

if [[ $# != 3 ]]; then
    echo -e "\033[41;36m Usage -> ./Script.sh AppName PortNum PROFILE \n(Notice -> profile must be one of these situations: dev,preview,online )\033[0m"
    exit 1
fi
cd ..
if [[ ! -f 'pom.xml' ]]; then
    echo -e "\033[41;36m pom.xml not found, May you put the script in wrong path \033[0m"
    exit 1
fi
if [[ ! -d '/var/lib/filesystem' ]]; then
    mkdir -p /var/lib/filesystem
fi
mvn clean&&mvn install
cd ${NAME}/&&mvn clean&&mvn install
cp ${NAME}/target/${NAME}-${VERSION}.jar ./app.jar
docker build -f Dockerfile-${PROFILE} . -t ${APP_NAME}
docker rm -f ${APP_NAME}||docker run -d -p ${PORT}:8080 \
-v /var/lib/filesystem:/filesystem \
--restart unless-stopped \
--name=${APP_NAME} ${APP_NAME}
docker logs -f ${APP_NAME}