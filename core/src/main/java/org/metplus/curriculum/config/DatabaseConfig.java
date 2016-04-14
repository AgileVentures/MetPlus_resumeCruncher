package org.metplus.curriculum.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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

    private String uri = null;
    /**
     * Class constructor
     */
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
        try {
            URI uriMongo = new URI(this.uri);
            this.host = uriMongo.getHost();
            this.port = uriMongo.getPort();
            this.username = uriMongo.getUserInfo().split(":")[0];
            this.password = uriMongo.getUserInfo().split(":")[1];
            this.name = uriMongo.getPath().substring(1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);
}
