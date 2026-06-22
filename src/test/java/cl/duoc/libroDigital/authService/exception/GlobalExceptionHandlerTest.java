package cl.duoc.libroDigital.authService.exception;

import cl.duoc.libroDigital.authService.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleRuntimeException_returnsBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleRuntimeException(new RuntimeException("Credenciales inválidas"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credenciales inválidas", response.getBody().getError());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(new Exception("fallo"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody().getError());
    }
}
