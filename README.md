# MetPlus_resumeCruncher
Resume Processor for the MetPlus project

# Requirements on development environment

- Java SDK version 8
- MongoDB

# Configuration files

- core/src/main/resources/database.yml

  In this file the configuration of connection to the database is stored

- app/src/main/resources/application.yml
 
  In this file the application configuration is stored

    1. Tomcat configuration
    1. Log level

# Installation
1. Clone this repository by using the command

  ```git clone https://github.com/AgileVentures/MetPlus_resumeCruncher.git resumeCruncher```

1. Create SSL key under app/ folder

  ```$JAVA_HOME/bin/keytool -genkey -alias tcserver -keyalg RSA -keystore KEYSTORE_FILENAME.p12 -storetype PKCS12```


1. Edit the configuration in the file 
  ```app/src/main/resources/application.yml``` to match the previous command options
  ```
server:
    ssl:
      key-store: KEYSTORE_FILENAME.p12
      key-store-password: password_added_at_the_file_creation
      keyStoreType: PKCS12
      keyAlias: tcserver
  ```

# Using the application

To send requests to the application a browser can be user pointing to the address
``` https://localhost:8443/.... ```

# Unit Testing
Before running the tests make sure to have mongodb launched in your machine or if you want to use one external server edit the file ```core/src/main/resources/database.yml``` with the new definition

After that just run the following command

```> SPRING_ACTIVE_PROFILE="unit-test" ./gradlew check ```
