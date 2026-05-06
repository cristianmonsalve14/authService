package cl.duoc.libroDigital.authService.dto;

import java.time.LocalDateTime;

public class ErrorResponse {

    private int status;
    private String error;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error) {
        this.status = status;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}