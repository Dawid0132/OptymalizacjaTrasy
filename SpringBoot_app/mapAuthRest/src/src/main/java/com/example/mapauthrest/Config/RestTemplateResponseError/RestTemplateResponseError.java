package com.example.mapauthrest.Config.RestTemplateResponseError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class RestTemplateResponseError {
    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<Void> handleNotFound(HttpClientErrorException.NotFound e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<Void> handleBadRequest(HttpClientErrorException.BadRequest e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<Void> handleInternalServer(HttpServerErrorException.InternalServerError e) {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Void> handleResourceAccess(ResourceAccessException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
    }
}
