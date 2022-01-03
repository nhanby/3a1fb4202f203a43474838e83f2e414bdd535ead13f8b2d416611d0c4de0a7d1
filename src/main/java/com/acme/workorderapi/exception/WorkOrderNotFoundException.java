package com.acme.workorderapi.exception;

import com.acme.workorderapi.AppConstants;

public class WorkOrderNotFoundException extends RuntimeException {
	
	public WorkOrderNotFoundException() {
		super(AppConstants.WORK_ORDER_NOT_FOUND_ERROR_MSG);
	}

}
