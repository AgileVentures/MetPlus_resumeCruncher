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


# Using the application

To start the application:

```./gradlew startCruncher```

To send requests to the application a browser can be user pointing to the address
``` http://localhost:8443/.... ```

The API documentation can be found in: http://joaopapereira.github.io/MetPlus_resumeCruncher/

To start you need to authenticate doing 
```
POST /api/v1/authenticate HTTP/1.1
Accept: application/json
X-Auth-Username: backend_admin
X-Auth-Password: backendpassword
Host: localhost
```

The answer will get you a token in the following request you need to include the token like:

```
GET /api/v1/curriculum/asdasdasd HTTP/1.1
X-Auth-Token: 377430e2-db04-4d10-b119-6a7448cbdc19
Host: localhost
```

# Unit Testing
Before running the tests make sure to have mongodb launched in your machine or if you want to use one external server edit the file ```core/src/main/resources/database.yml``` with the new definition

After that just run the following command

```> SPRING_ACTIVE_PROFILE="unit-test" ./gradlew check ```

# Credits
Naive Bayes Classifier is based on the source developed by Philipp Nolte.

The Initial source can be found in: 
https://github.com/ptnplanet/Java-Naive-Bayes-Classifier