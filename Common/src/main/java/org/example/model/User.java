package org.example.model;

import java.io.Serializable;

public class User implements Serializable {

    private long id;
    private String username;
    private String password;

    public User() {

    }

    public User(long id , String username, String password) {
        this.id = id;
        this.username = username;
        this.password= password;
    }

    public User(String password, String username) {
        this.password = password;
        this.username = username;
    }
    public static User.Builder builder() {
        return new User().new Builder();
    }


    public class Builder {

        private Builder() {

        }

        public User.Builder id(long id) {
            User.this.id = id;
            return this;
        }


        public User.Builder username(String username) {
            User.this.username = username;
            return this;
        }

        public User.Builder password(String password) {
            User.this.password = password;
            return this;
        }

        public User build() {
            return User.this;
        }


    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", passwordHash='" + password + '\'' +
               '}';
    }
}