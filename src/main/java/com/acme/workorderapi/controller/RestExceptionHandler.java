package com.acme.workorderapi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.acme.workorderapi.dto.ApiErrorResponse;
import com.acme.workorderapi.exception.WorkOrderAlreadyExistsException;
import com.acme.workorderapi.exception.WorkOrderNotFoundException;

//@Slf4j
@ControllerAdvice
//TODO - make sure to log the different error scenarios
//TODO - externalize error messages
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return buildResponseEntity(HttpStatus.BAD_REQUEST, new ApiErrorResponse("Malformed JSON request"));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiErrorResponse errorResponse = new ApiErrorResponse("Validation error occurred on item in the request body");
		errorResponse.addFieldValidationErrors(ex.getBindingResult().getFieldErrors());
		errorResponse.addGlobalValidationErrors(ex.getBindingResult().getGlobalErrors());
		return buildResponseEntity(HttpStatus.BAD_REQUEST, errorResponse);
	}

	@ExceptionHandler(WorkOrderNotFoundException.class)
	private ResponseEntity<Object> workOrderNotFoundExceptionHandler(WorkOrderNotFoundException wonfe) {
		return buildResponseEntity(HttpStatus.NOT_FOUND, new ApiErrorResponse(wonfe.getLocalizedMessage()));
	}

	@ExceptionHandler(WorkOrderAlreadyExistsException.class)
	private ResponseEntity<Object> workOrderAlreadyExistsExceptionHandler(WorkOrderAlreadyExistsException woaee) {
		return buildResponseEntity(HttpStatus.CONFLICT, new ApiErrorResponse(woaee.getLocalizedMessage()));
	}

	private ResponseEntity<Object> buildResponseEntity(HttpStatus status, ApiErrorResponse apiError) {
		return new ResponseEntity<>(apiError, status);
	}

}