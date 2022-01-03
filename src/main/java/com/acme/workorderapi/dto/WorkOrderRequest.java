package com.acme.workorderapi.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.acme.workorderapi.AppConstants;

public class WorkOrderRequest {

	@NotNull
	@Digits(fraction = 0, integer = 19, message = AppConstants.REQUESTOR_ID_DIGITS_VALIDATION_ERROR_MSG)
	@Range(min = 1L, max = 9223372036854775807L, message = AppConstants.REQUESTOR_ID_RANGE_VALIDATION_ERROR_MSG)
	private String requestorId;

	@NotNull
	private LocalDateTime timeAdded;
	
	public WorkOrderRequest(String requestorId, LocalDateTime timeAdded) {
		this.requestorId = requestorId;
		this.timeAdded = timeAdded;
	}

	public Long getRequestorId() {
		return Long.valueOf(this.requestorId);
	}
	
	public LocalDateTime getTimeAdded() {
		return this.timeAdded;
	}
}
