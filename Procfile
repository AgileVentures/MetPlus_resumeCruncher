web: java $JAVA_OPTS -Dserver.port=$PORT \
      -Ddatabase-pets.uri=$MONGOLAB_URI \
      -Dbackend.admin.username=${APP_USERNAME} \
      -Dbackend.admin.password=${APP_PASSWORD} \
      -jar app/build/libs/app.jar --spring.profiles.active=production