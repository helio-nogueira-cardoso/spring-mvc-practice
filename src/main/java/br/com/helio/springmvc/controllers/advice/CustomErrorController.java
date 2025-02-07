package br.com.helio.springmvc.controllers.advice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomErrorController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<List<Map<String, String>>> handleBindErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errorList = ex.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).toList();
        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler(TransactionSystemException.class)
    ResponseEntity<?> handleJPAViolation(TransactionSystemException ex) {
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.badRequest();

        if (ex.getCause().getCause() instanceof ConstraintViolationException ve) {
            List<Map<String, String>> errors = ve.getConstraintViolations()
                    .stream()
                    .map(constraintViolation -> {
                        Map<String, String> violation = new HashMap<>();
                        violation.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return violation;
                    })
                    .toList();

            return bodyBuilder.body(errors);
        }

        return bodyBuilder.build();
    }
}