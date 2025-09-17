package com.example.bankcards.exception;

import com.example.bankcards.exception.error.AccessDeniedException;
import com.example.bankcards.exception.error.BadRequestException;
import com.example.bankcards.exception.error.CardAlreadyExistsByCardNumberException;
import com.example.bankcards.exception.error.CardNotFoundById;
import com.example.bankcards.exception.error.ConflictException;
import com.example.bankcards.exception.error.UnauthorizedException;
import com.example.bankcards.exception.error.UserAlreadyExistsByEmailException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler({
        CardNotFoundById.class,
        UsernameNotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(
        Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .errorDescription(ex.getMessage())
            .statusCode(HttpStatus.NOT_FOUND.value())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler({
        UnauthorizedException.class,
        BadCredentialsException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(
        Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .errorDescription(ex.getMessage())
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler({
        AccessDeniedException.class,
        org.springframework.security.access.AccessDeniedException.class,
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(
        Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
            .error(HttpStatus.FORBIDDEN.getReasonPhrase())
            .errorDescription(ex.getMessage())
            .statusCode(HttpStatus.FORBIDDEN.value())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(
        Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .errorDescription(ex.getMessage())
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler({
        ConflictException.class,
        CardAlreadyExistsByCardNumberException.class,
        UserAlreadyExistsByEmailException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(
        Exception ex, HttpServletRequest request) {

        return ErrorResponse.builder()
            .error(HttpStatus.CONFLICT.getReasonPhrase())
            .errorDescription(ex.getMessage())
            .statusCode(HttpStatus.CONFLICT.value())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
            .map(error -> {
                if (error instanceof FieldError fieldError) {
                    return String.format("%s: %s", fieldError.getField(), error.getDefaultMessage());
                }
                return error.getDefaultMessage();
            })
            .collect(Collectors.joining("; "));

        return ErrorResponse.builder()
            .error(String.valueOf(HttpStatus.BAD_REQUEST))
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .path(request.getRequestURI())
            .errorDescription(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse constraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
            .map(violation -> String.format("%s: %s",
                violation.getPropertyPath().toString(),
                violation.getMessage()))
            .collect(Collectors.joining("; "));

        return ErrorResponse.builder()
            .error(String.valueOf(HttpStatus.BAD_REQUEST))
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .path(request.getRequestURI())
            .errorDescription(errorMessage)
            .timestamp(LocalDateTime.now())
            .build();
    }

}
