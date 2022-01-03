package com.acme.workorderapi.exception;

public class EmptyQueueException extends RuntimeException {

	public EmptyQueueException(String message) {
		super(message);
	}
	
}