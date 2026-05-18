package cl.duoc.libroDigital.authService.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    // ✅ getter accessToken
    public String getAccessToken() {
        return accessToken;
    }

    // ✅ setter accessToken
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // ✅ getter refreshToken
    public String getRefreshToken() {
        return refreshToken;
    }

    // ✅ setter refreshToken
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}