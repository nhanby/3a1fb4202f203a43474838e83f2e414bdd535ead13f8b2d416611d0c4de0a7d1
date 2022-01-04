package com.acme.workorderapi;

public class TestConstants {
	public static final String WORK_ORDERS_API_BASE_PATH = "/api/v1/workorders/";
	public static final String DEQUEUE_URL = WORK_ORDERS_API_BASE_PATH + "dequeue";
	public static final String ENQUEUE_URL = WORK_ORDERS_API_BASE_PATH + "enqueue";
	public static final String LIST_IDS_URL = WORK_ORDERS_API_BASE_PATH + "listids";
	public static final String POSITION_URL =  WORK_ORDERS_API_BASE_PATH + "position/{id}";
	public static final String WORK_ORDER_URL = WORK_ORDERS_API_BASE_PATH + "{id}";
	public static final String GET_AVERAGE_WAIT_TIME_URL = WORK_ORDERS_API_BASE_PATH + "/avgWaitTime/{currentTime}";
}
