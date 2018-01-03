package com.interlinguatts;

public class Credentials {
    private final String user;
    private final String password;
    private final String endpoint;

    public Credentials(String user, String password, String endpoint) {
        this.user = user;
        this.password = password;
        this.endpoint = endpoint;
    }

    public Credentials(String user, String password) {
        this.user = user;
        this.password = password;
        this.endpoint = null;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
