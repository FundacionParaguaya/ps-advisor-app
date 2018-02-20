package org.fundacionparaguaya.advisorapp.models;

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

        Login login = (Login) o;

        if (getExpiresIn() != login.getExpiresIn()) return false;
        if (getAccessToken() != null ? !getAccessToken().equals(login.getAccessToken()) : login.getAccessToken() != null)
            return false;
        if (getTokenType() != null ? !getTokenType().equals(login.getTokenType()) : login.getTokenType() != null)
            return false;
        return getRefreshToken() != null ? getRefreshToken().equals(login.getRefreshToken()) : login.getRefreshToken() == null;
    }

    @Override
    public int hashCode() {
        int result = getAccessToken() != null ? getAccessToken().hashCode() : 0;
        result = 31 * result + (getTokenType() != null ? getTokenType().hashCode() : 0);
        result = 31 * result + getExpiresIn();
        result = 31 * result + (getRefreshToken() != null ? getRefreshToken().hashCode() : 0);
        return result;
    }
}
