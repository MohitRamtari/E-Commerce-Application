package com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionResponse> handleUserAlreadyExistException(
            UserAlreadyExistException ex) {
        ExceptionResponse err = new ExceptionResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleUsernameNotFoundException(
            ResourceAlreadyExistException ex) {
        ExceptionResponse err = new ExceptionResponse((HttpStatus.NOT_FOUND.value()), ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        ExceptionResponse err = new ExceptionResponse((HttpStatus.BAD_REQUEST.value()), ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(value = ResourcesNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleResourcesNotFoundException(
            ResourcesNotFoundException ex) {
        ExceptionResponse err = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(value = AccountActivationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionResponse> handleAccountActivationException(
            ResourceAlreadyExistException ex) {
        ExceptionResponse err = new ExceptionResponse((HttpStatus.UNAUTHORIZED.value()),
                ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(value = ResourceAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionResponse> handleResourceAlreadyExistException(
            ResourceAlreadyExistException ex) {
        ExceptionResponse err = new ExceptionResponse((HttpStatus.CONFLICT.value()), ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionResponse> handleInvalidCredentialsException(
            InvalidCredentialsException ex) {
        ExceptionResponse err = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(value = FileStorageException.class)
    @ResponseStatus()
    public ResponseEntity<ExceptionResponse> handleFileStorageException(
            ResourceAlreadyExistException ex) {
        ExceptionResponse err = new ExceptionResponse(500, ex.getMessage());
        return new ResponseEntity<>(err, null, 500);
    }

    @ExceptionHandler(value = ProductStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handleProductStatusException(
            ResourceAlreadyExistException ex) {
        ExceptionResponse err = new ExceptionResponse((HttpStatus.NOT_FOUND.value()),
                ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(value = CommonValidationFailedException.class)
    @ResponseStatus()
    public ResponseEntity<ExceptionResponse> handleCommonValidationFailedException(
            CommonValidationFailedException ex) {
        ExceptionResponse err = new ExceptionResponse((HttpStatus.BAD_REQUEST.value()),
                ex.getMessage());
        return new ResponseEntity<>(err, null, HttpStatus.BAD_REQUEST.value());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ValidationErrors> errorList = new ArrayList<>();
        for (int i = 0; i < ex.getBindingResult().getErrorCount(); i++) {
            ValidationErrors obj = new ValidationErrors();
            obj.setDefaultMessage(ex.getBindingResult().getFieldErrors().get(i).getDefaultMessage());
            obj.setField(ex.getBindingResult().getFieldErrors().get(i).getField());
            errorList.add(obj);
        }
        return new ResponseEntity<Object>(
                new ValidationExceptionList(HttpStatus.BAD_REQUEST.value(), "Invalid Request",
                        errorList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(ConstraintViolationException exception) {
        String errorMessage = new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage();
        ExceptionResponse apiError = new ExceptionResponse(100, errorMessage);
        return new ResponseEntity<ExceptionResponse>(apiError, null, HttpStatus.BAD_REQUEST);
    }
}
