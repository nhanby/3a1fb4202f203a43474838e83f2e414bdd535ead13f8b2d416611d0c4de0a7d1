package com.acme.workorderapi.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
public class ApiErrorResponse {

	private String message;
	private List<ApiValidationError> details;

	public ApiErrorResponse(String message) {
		this.message = message;
	}

	public void addValidationError(ApiValidationError subError) {
		if (details == null) {
			details = new ArrayList<>();
		}
		details.add(subError);
	}

	private void addValidationError(String object, String field, Object rejectedValue, String message) {
		addValidationError(new ApiValidationError(object, field, rejectedValue, message));
	}

	private void addValidationError(String object, String message) {
		addValidationError(new ApiValidationError(object, message));
	}

	private void addValidationError(FieldError fieldError) {
		this.addValidationError(fieldError.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(),
				fieldError.getDefaultMessage());
	}

	public void addFieldValidationErrors(List<FieldError> fieldErrors) {
		fieldErrors.forEach(this::addValidationError);
	}

	private void addValidationError(ObjectError objectError) {
		this.addValidationError(objectError.getObjectName(), objectError.getDefaultMessage());
	}

	public void addGlobalValidationErrors(List<ObjectError> globalErrors) {
		globalErrors.forEach(this::addValidationError);
	}

}