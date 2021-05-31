package br.udesc.esag.participactbrasil.domain.local;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Set;

public class Account implements Serializable {

    private static final long serialVersionUID = -6503864251024997229L;

    private Long id;

    private String username;
    private String password;
    private DateTime lastLogin;
    private DateTime creationDate;

    private Set<User> createdUsers;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(DateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String toString() {
        return String.format("%s", username);
    }

    public Set<User> getCreatedUsers() {
        return createdUsers;
    }

    public void setCreatedUsers(Set<User> createdUsers) {
        this.createdUsers = createdUsers;
    }
}
