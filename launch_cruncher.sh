#!/bin/sh
set -x
env
java $JAVA_OPTS -Dserver.port=$PORT -Dspring.data.mongodb.uri=$MONGODB_URI -Dbackend.admin.username=${APP_USERNAME} -Dbackend.admin.password=${APP_PASSWORD} -jar /cruncher/cruncher.jar --spring.profiles.active=production

