package cl.duoc.libroDigital.authService.exception;

/** Recurso en conflicto (p. ej. usuario o email ya existente). HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
