package cl.duoc.libroDigital.authService.exception;

import cl.duoc.libroDigital.authService.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUnauthorized_returnsUnauthorized() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorized(new UnauthorizedException("Credenciales inválidas"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody().getError());
    }

    @Test
    void handleBadRequest_returnsBadRequest() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequest(new BadRequestException("dato inválido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("dato inválido", response.getBody().getError());
    }

    @Test
    void handleNotFound_returnsNotFound() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new NotFoundException("no existe"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("no existe", response.getBody().getError());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(new Exception("fallo"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody().getError());
    }
}
