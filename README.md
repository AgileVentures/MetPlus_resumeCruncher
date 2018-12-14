# MetPlus_resumeCruncher
Resume Processor for the MetPlus project

# Requirements on development environment

- Java SDK version 8
- MongoDB

# Installation
1. Clone this repository by using the command

  ```git clone https://github.com/AgileVentures/MetPlus_resumeCruncher.git resumeCruncher```
  
2. Enter the source folder (All commands in this document assume you are currently inside the source folder directory)

  ```cd resumeCruncher```

## Database
In order to install and run the application you need to have access to one mongo instance to save the cruncher information.
You can use a Local Mongo instance, a Docker container, a Virtual Machine or a database as a Service like [mlab](http://www.mlab.com)

### Examples of installation of mongo
In the following there is more information on how to create a mongo database
#### (Example) Local mongo database

Follow the instructions on [mongodb.com](https://www.mongodb.com/download-center?jmp=nav#community) to install mongo on your operating system

When this is done jump to the mongo database configuration section
#### (Example) Mongo in docker container

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

### Mongo configuration

Before we can start the Cruncher, we need to create a database to connect to and a user for authentication. 
To accomplish that connect to the Mongo instance using mongo CLI or a a visual app like robomongo and execute the following:

```mongo
 use resumeCruncher
 db.createUser({user: 'testing_user', pwd: 'testing_user', roles: [{role: 'readWrite', db: 'resumeCruncher'}]});
```

# Configuration files

- app/src/main/resources/application.yml
 
  In this file the application configuration is stored

    1. Tomcat configuration
    1. Log level
    1. Database connection information
    1. Default cruncher configuration
 
 ## Structure of the configuration
 
 The configuration file is structures as per Spring using a multi profile following the [example](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-multi-profile-yaml)
 
 We are using a default configuration plus specific configuration for `production`, `development` and `unit-database` profiles.
 
 To launch with a specific profile the application should be launched with the following command:
 
 ```bash
 $ SPRING_PROFILES_ACTIVE="production" ./gradlew startCruncher
 ```
 
 The default profile is `development`
 
 ## Tomcat configuration
 
 The only change to tomcat configuration is the server port and it looks like this:
 
 ```
 server:
  port: 8443
 ```

Other options are available, please refer to the documentation of spring boot

## Log level

The application is using the default logger of spring boot.
To change the log level of the framework and libraries:
```
logging.level: INFO
logging.level.org.hibernate: ERROR
logging.level.com.mongodb: ERROR
logging.level.org.springframework: WARN
```

To change the Cruncher specific logs:
```
logging.level.org.metplus: DEBUG
```

## Database connection information
The database configuration can be found in blocks like the following and there is one for each profile.

```
database-pets:
    username: username      # Username to connect to the mongo database
    password: password      # Password of the User in mongo database
    name: resumeCruncher    # Name of the database in mongo
    host: localhost         # Hostname where the mongo database is running
    port: 27017             # Port the mongo databse is listening
```

Make sure to update the correct section of this file depending on the profile that is going to be used.

### Optional configuration
All the previous configuration can also be archived by using the URI to the database. In order to use this URI replace the previous section with the next one

```
database-pets:
    uri: mongodb://username:password@hostname:port/database_name
```

## Default cruncher configuration

### Web application configuration
In this section it is possible to change the username and password used to connect to the cruncher server

```
backend:
  admin:
    username: backend_admin     # Username that should be provided in the HTTP parameter X-Auth-Username
    password: backendpassword   # Password that should be provided in the HTTP parameter X-Auth-Password 
```

### Cunchers default configuration

This section of configuration is the section that uses more space, because it contains the initial configuration for all the crunchers.

At this point there are 2 crunchers and they have the following configuration:

#### Expression based cruncher
This cruncher is a very basic implementation of a word/expression counting algorithm, that adds up all the occurencies of a word/expression and based on the words/expression more common matches documents.

The options for this cruncher looks like this

```
expression-cruncher:
    case-sensitive: false                      # Should the matching be case sensitive or not
    ignoreListWordSearch: true                 # Ignore the words in the ignore list
    merge-list:                                # List of tokens to be merged, this is a hash that contains
      "software development":                  # the final name and a list of expressions that will be replace by that final name
        - "software development"
        - "software development lifecycle"
      "cook":
        - "cook"
        - "line cook"
    ignore-list:                               # List of words or expressions that will be ignored when matching
      - "a"                                    # this is used to archive more accuracy on the matching
```

#### Naive Bayes based cruncher
This cruncher uses a different approach, based in the Naive Bayes algorithm to match categories. 
This algorithm need a set of trainning documments for each possible category. The bigger the set the more accurate is the algorithm.

```
naive-bayes:
  learn-database:              # Trainning set, this is a hash that contains the category name
    "category one":            # and a list with different documents that belong to each category
      - "blah blah blah"
    "category two":
      - "bamm bamm bamm"
  clean-expressions:           # To ensure that common words do not polute the results, we can
    - "a"                      # can provide a list of words or expressions that will be removed
    - "or"                     # from the document before the algorithm run
```

# Using the application

Enter the sources folder before doing the next step

To start the application:

```./gradlew startCruncher```

To send requests to the application a browser can be user pointing to the address
``` http://localhost:8443/.... ```

The API documentation can be found in: http://joaopapereira.github.io/MetPlus_resumeCruncher/

To start you need to authenticate: 

```
POST /api/v1/authenticate HTTP/1.1
Accept: application/json
X-Auth-Username: backend_admin
X-Auth-Password: backendpassword
Host: localhost
```

The response will include a token. In subsequent requests you will need to include the token as follows:

```
GET /api/v1/curriculum/asdasdasd HTTP/1.1
X-Auth-Token: 377430e2-db04-4d10-b119-6a7448cbdc19
Host: localhost
```

# Unit Testing
Before running the tests make sure to have mongodb launched in your machine or if you want to use one external server edit the file ```app/src/main/resources/application.yml``` with the new definition

After that just run the following command

```> SPRING_ACTIVE_PROFILE="unit-test" ./gradlew check ```

# Contributing

1. Fork it ( https://github.com/AgileVentures/MetPlus_resumeCruncher/fork )
1. Talk with us in [Slack](https://agileventures.slack.com) channel #metplus
1. Find a story to work in [Waffle](https://waffle.io/AgileVentures/MetPlus_tracker?search=resumecruncher)
1. Create your feature branch (git checkout -b my-new-feature)
1. Test changes don't break anything (```./gradlew check```)
1. Add test for your new feature
1. Commit your changes (`git commit -am 'Add some feature'`)
1. Push to the branch (`git push origin my-new-feature`)
1. Create a new Pull Request

# Credits
Naive Bayes Classifier is based on the source developed by Philipp Nolte.

The Initial source can be found in: 
https://github.com/ptnplanet/Java-Naive-Bayes-Classifier
