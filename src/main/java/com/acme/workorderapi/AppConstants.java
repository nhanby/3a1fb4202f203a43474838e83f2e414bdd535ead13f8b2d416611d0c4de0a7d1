package com.acme.workorderapi;

public class AppConstants {
	
	private AppConstants() {}
	
	public static final String WORK_ORDERS_API_BASE_PATH = "/api/v1/workorders/";
	
	public static final String REQUESTOR_ID_DIGITS_VALIDATION_ERROR_MSG = "requestorId must be an integer value with a maximum of 19 digits"; 
	public static final String REQUESTOR_ID_RANGE_VALIDATION_ERROR_MSG = "requestorId must be an integer value ranging between 1 to 9223372036854775807";
	public static final String EMPTY_QUEUE_ERROR_MSG = "Nothing to dequeue, queue is empty";
	public static final String WORK_ORDER_ALREADY_EXISTS_ERROR_MSG = "A work order with id={0} already exists";
	public static final String WORK_ORDER_NOT_FOUND_ERROR_MSG = "No work order with id={0} found";
}
