package org.metplus.curriculum.web.controllers;

/**
 * Created by joaopereira on 2/14/2016.
 */
public class User {
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
