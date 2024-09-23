package dca.pfe_ensa.MODEL;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("message")
    private String message;

    @JsonProperty("role")
    private String role;

    public AuthResponse(String accessToken, String refreshToken, String message, Role role) {
        this.accessToken = accessToken;
        this.message = message;
        this.refreshToken = refreshToken;
        this.role = String.valueOf(role);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }
}
