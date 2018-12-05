FROM openjdk:8-jdk

RUN mkdir /cruncher
WORKDIR /cruncher
COPY . /cruncher
RUN ./gradlew stage
RUN mv /cruncher/app/build/libs/app.jar /cruncher/cruncher.jar

CMD exec java $JAVA_OPTS -Dserver.port=$PORT -Ddatabase-pets.uri=$MONGOLAB_URI -Dbackend.admin.username=${APP_USERNAME} -Dbackend.admin.password=${APP_PASSWORD} -jar /cruncher/cruncher.jar --spring.profiles.active=production