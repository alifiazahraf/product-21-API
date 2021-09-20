/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.vutura.p21.exceptions;

import io.vutura.p21.util.JsonEnvelope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ahmad R. Djarkasih
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class.getName());

    private final Map<String, String> integrityErrorTemplate = Map.of(
            "isbn", "Book with isbn = %s is already existed"
    );

    private HttpStatus getHttpStatus(CrudException.ExceptionType kind) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        switch (kind) {
            case DataNotFound: {
                status = HttpStatus.NOT_FOUND;
                break;
            }
            case DataIntegrityViolation:
            case DataExisted: {
                status = HttpStatus.BAD_REQUEST;
                break;
            }
        }

        return status;

    }

    @ExceptionHandler(value = {CrudException.class})
    public ResponseEntity<Map<String, Object>> handleCrudException(CrudException ex) {

        Map<String, Object> env;

        env = JsonEnvelope.encloseErrorData(
                getHttpStatus(ex.getKind()).value(),
                ex.getMessage()
        );

        return new ResponseEntity<Map<String, Object>>(env, HttpStatus.OK);

    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Map<String, Object>> handleAllException(Exception ex) {

        LOG.log(Level.INFO, "Exception Classname = {0}", ex.getClass().getCanonicalName());
        LOG.log(Level.INFO, "Exception Message = {0}", ex.getMessage());

        Map<String, Object> env;

        env = JsonEnvelope.encloseErrorData(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        );

        return new ResponseEntity<Map<String, Object>>(env, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleDataIntegrityError(DataIntegrityViolationException ex) {
        LOG.log(Level.INFO, "Exception Classname = {0}", ex.getClass().getCanonicalName());
        LOG.log(Level.INFO, "Exception Message = {0}", ex.getMessage());

        Map<String, Object> env;

        String msg = ex.getMessage();
        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cvex = (ConstraintViolationException) ex.getCause();
            msg = cvex.getConstraintViolations().toString();
        }

        env = JsonEnvelope.encloseErrorData(
                HttpStatus.BAD_REQUEST.value(),
                msg
        );

        return new ResponseEntity<Map<String, Object>>(env, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> env = JsonEnvelope.encloseErrorData(
                HttpStatus.BAD_REQUEST.value(),
                String.format("No handler found for %s path", ex.getRequestURL())
        );

        return new ResponseEntity<Object>(env, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> env;

        Map<String, String> errors = new LinkedHashMap();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        env = JsonEnvelope.encloseErrorData(
                HttpStatus.BAD_REQUEST.value(),
                "Data Input Error",
                errors
        );

        return new ResponseEntity<Object>(env, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> env;

        env = JsonEnvelope.encloseErrorData(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return new ResponseEntity<Object>(env, HttpStatus.OK); // vutura bug. only proceed if 200 only

    }

}
