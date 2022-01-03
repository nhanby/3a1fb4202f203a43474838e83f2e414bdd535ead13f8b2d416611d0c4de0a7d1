package com.acme.workorderapi.exception;

import com.acme.workorderapi.AppConstants;

public class WorkOrderAlreadyExistsException extends RuntimeException {
	
	public WorkOrderAlreadyExistsException() {
		super(AppConstants.WORK_ORDER_ALREADY_EXISTS_ERROR_MSG);
	}

}
