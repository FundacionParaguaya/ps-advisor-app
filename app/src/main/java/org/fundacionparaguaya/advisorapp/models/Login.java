package org.fundacionparaguaya.advisorapp.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The login of a user.
 */

public class Login {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private String refreshToken;

    public Login(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Login(String accessToken, String tokenType, int expiresIn, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAuthenticationString() {
        return tokenType + " " + accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Login that = (Login) o;

        return new EqualsBuilder()
                .append(accessToken, that.accessToken)
                .append(tokenType, that.tokenType)
                .append(expiresIn, that.expiresIn)
                .append(refreshToken, that.refreshToken)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 97)
                .append(accessToken)
                .append(tokenType)
                .append(expiresIn)
                .append(refreshToken)
                .toHashCode();
    }
}
