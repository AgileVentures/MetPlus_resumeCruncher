# MetPlus_resumeCruncher
Resume Processor for the MetPlus project

# Requirements on development environment

- Java SDK version 8
- MongoDB

# Configuration files

- app/src/main/resources/application.yml
 
  In this file the application configuration is stored

    1. Tomcat configuration
    1. Log level
    1. Database connection information
    1. Default cruncher configuration

# Installation
1. Clone this repository by using the command

  ```git clone https://github.com/AgileVentures/MetPlus_resumeCruncher.git resumeCruncher```

## Database

### Local mongo database

Follow the instruction to on [mongodb.com](https://www.mongodb.com/download-center?jmp=nav#community) to install in your operating system the database

When this is done jump to the mongo database configuration section
### Mongo in docker container

Requirement for this options is to have Docker installed in your system.

1 - Create docker image: 
```bash
docker run -it -p 27017:27017 --name pets-mongo -d mongo --noauth
```
This command creates a new image called `pets-mongo` and publishes the mongo port so
we can access it from the host machine

2 - Access the mongo instance to create the user and the database

```bash
docker exec -it pets-mongo mongo resumeCruncher
```

### Mongo configuration section

Connect to the database resumeCruncher.

1 - Create the user on the database

```mongo
 db.createUser({user: 'testing_user', pwd: 'testing_user', roles: [{role: 'readWrite', db: 'resumeCruncher'}]});
```


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
Before running the tests make sure to have mongodb launched in your machine or if you want to use one external server edit the file ```app/src/main/resources/application.yml``` with the new definition

After that just run the following command

```> SPRING_ACTIVE_PROFILE="unit-test" ./gradlew check ```


# Credits
Naive Bayes Classifier is based on the source developed by Philipp Nolte.

The Initial source can be found in: 
https://github.com/ptnplanet/Java-Naive-Bayes-Classifier
