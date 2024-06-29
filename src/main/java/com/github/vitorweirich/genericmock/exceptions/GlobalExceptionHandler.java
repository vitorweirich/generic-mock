package com.github.vitorweirich.genericmock.exceptions;

import java.time.LocalDateTime;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ControllerAdvice
public class GlobalExceptionHandler {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String mockPath;
        private String errorMessage;
        private String errorStackTrace;
	}
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
		
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "%s_%s".formatted(request.getMethod(), request.getRequestURI()),
                ex.getMessage(),
                ExceptionUtils.getStackTrace(ex)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
