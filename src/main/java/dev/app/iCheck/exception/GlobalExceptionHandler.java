package dev.app.iCheck.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // Przechwytujemy wszystkie wyjątki
    public ResponseEntity<String> handleException(Exception e) {
        // Możesz logować wyjątek tutaj, jeśli chcesz
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił błąd: " + e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class) // Obsługa specyficznego wyjątku
    public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Użytkownik nie znaleziony: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class) // Obsługa RuntimeException (np. błędne dane logowania)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nieprawidłowe dane: " + e.getMessage());
    }

}