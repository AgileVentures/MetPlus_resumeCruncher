package org.metplus.curriculum.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Joao Pereira on 27/08/2015.
 */
@Component
@ConfigurationProperties(locations = {"classpath:database.yml"},prefix="pets-db")
public class DatabaseConfig {

    private String name;
    private String username;
    private String password;
    private int port;
    private String host;
    public DatabaseConfig(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }



    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public boolean asAuthentication() {return !(username == null);}

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);
}
