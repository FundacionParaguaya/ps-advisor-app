package org.fundacionparaguaya.advisorapp.data.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A user which can use the application.
 */

public class User {
    private String username;
    private String password;
    private Login login;

    public User(String username, String password, Login login) {
        this.username = username;
        this.password = password;
        this.login = login;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() { return password; }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private Login login;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder login(Login login) {
            this.login = login;
            return this;
        }

        public User build() {
            return new User(username, password, login);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;

        return new EqualsBuilder()
                .append(username, that.username)
                .append(login, that.login)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(31, 13)
                .append(username)
                .append(login)
                .toHashCode();
    }
}
