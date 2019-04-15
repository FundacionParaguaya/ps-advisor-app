package org.fundacionparaguaya.adviserplatform.data.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The login of a user.
 */

public class Login {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private String refreshToken;
    private User user;

    public Login(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Login(String accessToken, String tokenType, int expiresIn, String refreshToken) {
        this(accessToken, tokenType, expiresIn, refreshToken, null);
    }

    public Login(String accessToken, String tokenType, int expiresIn, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Login rhs = (Login) obj;
        return new EqualsBuilder()
                .append(this.accessToken, rhs.accessToken)
                .append(this.tokenType, rhs.tokenType)
                .append(this.expiresIn, rhs.expiresIn)
                .append(this.refreshToken, rhs.refreshToken)
                .append(this.user, rhs.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(accessToken)
                .append(tokenType)
                .append(expiresIn)
                .append(refreshToken)
                .append(user)
                .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("accessToken", accessToken)
                .append("tokenType", tokenType)
                .append("expiresIn", expiresIn)
                .append("refreshToken", refreshToken)
                .append("user", user)
                .toString();
    }
}
