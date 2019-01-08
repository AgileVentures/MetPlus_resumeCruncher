FROM openjdk:8-jdk

RUN mkdir /cruncher
WORKDIR /cruncher
COPY . /cruncher
RUN ./gradlew web:bootjar
RUN mv /cruncher/web/build/libs/web.jar /cruncher/cruncher.jar

CMD exec java $JAVA_OPTS -Dserver.port=$PORT -Dspring.data.mongodb.uri=$MONGODB_URI -Dbackend.admin.username=${APP_USERNAME} -Dbackend.admin.password=${APP_PASSWORD} -jar /cruncher/cruncher.jar --spring.profiles.active=production