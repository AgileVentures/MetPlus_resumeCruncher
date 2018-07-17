package org.metplus.curriculum.database.config;


import com.mongodb.*;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.repository.RepositoryPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = RepositoryPackage.class)
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.metplus.curriculum.database"})
public class SpringMongoConfig extends AbstractMongoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(SpringMongoConfig.class);
    @Autowired
    private DatabaseConfig dbConfig;

    @Override
    protected String getDatabaseName() {
        return dbConfig.getName();
    }


    private MongoClient withAuthentication()  throws Exception {
        MongoCredential a = MongoCredential.createCredential(dbConfig.getUsername(),
                getDatabaseName(),
                dbConfig.getPassword().toCharArray());
        ArrayList<MongoCredential> arr = new ArrayList<>();
        arr.add(a);
        ServerAddress addr = new ServerAddress(dbConfig.getHost(),
                dbConfig.getPort());

        MongoClient client = new MongoClient(addr,
                a,
                new MongoClientOptions.Builder().writeConcern(WriteConcern.ACKNOWLEDGED).build());
        return client;
    }

    private MongoClient withoutAuthentication() throws Exception {
        ServerAddress addr = new ServerAddress(dbConfig.getHost(),
                dbConfig.getPort());
        MongoClient client = new MongoClient(addr,
                new MongoClientOptions.Builder().writeConcern(WriteConcern.ACKNOWLEDGED).build());
        return client;
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Collections.singleton("org.metplus.curriculum.database.domain");
    }

    // ---------------------------------------------------- MongoTemplate

    @Override
    public MongoClient mongoClient() {
        try {
            logger.info("mongo connection to database: " + dbConfig.getHost() + ":" + dbConfig.getPort() + "/" + dbConfig.getName());
            logger.info("mongo connection uri to database: " + dbConfig.getUri());
            if (dbConfig.asAuthentication())
                return withAuthentication();
            else
                return withoutAuthentication();
        } catch (Exception exp) {
            return null;
        }
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}