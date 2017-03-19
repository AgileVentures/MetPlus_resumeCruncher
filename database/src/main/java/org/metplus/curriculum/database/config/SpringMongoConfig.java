package org.metplus.curriculum.database.config;


import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
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
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

import java.util.ArrayList;

@Configuration
@EnableMongoRepositories(basePackageClasses = RepositoryPackage.class)
@EnableAutoConfiguration
@ComponentScan(basePackages={"org.metplus.curriculum.database"})
public class SpringMongoConfig extends AbstractMongoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(SpringMongoConfig.class);
    @Autowired
    private DatabaseConfig dbConfig;

    @Override
    protected String getDatabaseName() {
        return dbConfig.getName();
    }


    @Override
    @Bean
    public MongoClient mongo() throws Exception {
        logger.info("mongo connection to database: " + dbConfig.getHost() + ":" + dbConfig.getPort() + "/" + dbConfig.getName());
        logger.info("mongo connection uri to database: " + dbConfig.getUri());
        if(dbConfig.asAuthentication())
            return withAuthentication();
        else
            return withoutAuthentication();
    }
    private MongoClient withAuthentication()  throws Exception {
        MongoCredential a = MongoCredential.createCredential(dbConfig.getUsername(),
                getDatabaseName(),
                dbConfig.getPassword().toCharArray());
        ArrayList<MongoCredential> arr = new ArrayList<>();
        arr.add(a);
        ServerAddress addr = new ServerAddress(dbConfig.getHost(),
                dbConfig.getPort());

        MongoClient client = new MongoClient(addr, arr);
        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }
    private MongoClient withoutAuthentication()  throws Exception {
        ServerAddress addr = new ServerAddress(dbConfig.getHost(),
                dbConfig.getPort());
        MongoClient client = new MongoClient(addr);
        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }

    @Override
    protected String getMappingBasePackage() {
        return "org.metplus.curriculum.database.domain";
    }

    // ---------------------------------------------------- MongoTemplate

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }
}