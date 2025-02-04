package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpStatus> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
