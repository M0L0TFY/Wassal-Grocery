package com.Wassal.exception;

import com.Wassal.dto.ExceptionMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    //403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionMessageResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionMessageResponse("You don't have permission to perform this action."));
    }
    //404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionMessageResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionMessageResponse(ex.getMessage()));
    }
    //409
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionMessageResponse> handleAlreadyExists(AlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionMessageResponse(ex.getMessage()));
    }
    //General exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessageResponse> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionMessageResponse("An unexpected server error occurred."));
    }

}
