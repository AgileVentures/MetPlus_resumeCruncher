package org.metplus.curriculum.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Joao Pereira on 27/08/2015.
 */
@Component
@ConfigurationProperties(prefix = "database-pets")
public class DatabaseConfig {


    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfig.class);
    private String name;
    private String username;
    private String password;
    private int port;
    private String host;
    private String uri = null;

    public DatabaseConfig() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.uri == null)
            this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (this.uri == null)
            this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (this.uri == null)
            this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (this.uri == null)
            this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if (this.uri == null)
            this.host = host;
    }

    public boolean asAuthentication() {
        return !(username == null);
    }

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
}
