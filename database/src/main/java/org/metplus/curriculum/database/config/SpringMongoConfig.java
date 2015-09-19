package org.metplus.curriculum.database.config;


import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.converters.ConvertersPackage;
import org.metplus.curriculum.database.repository.RepositoryPackage;
import org.metplus.curriculum.database.template.TemplatePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(basePackageClasses = RepositoryPackage.class)
@ComponentScan(basePackageClasses={TemplatePackage.class, ConvertersPackage.class})
@EnableAutoConfiguration
public class SpringMongoConfig extends AbstractMongoConfiguration {

    @Autowired
    private DatabaseConfig dbConfig;
    @Override
    protected String getDatabaseName() {
        return dbConfig.getName();
    }


    @Override
    @Bean
    public MongoClient mongo() throws Exception {
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