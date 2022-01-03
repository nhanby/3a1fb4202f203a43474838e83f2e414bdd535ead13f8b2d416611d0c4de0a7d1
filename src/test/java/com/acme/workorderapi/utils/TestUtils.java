package com.acme.workorderapi.utils;

import java.time.LocalDateTime;

import com.acme.workorderapi.domain.WorkOrderEntity;

public class TestUtils {
	
	public static WorkOrderEntity createWorkOrderEntity(long requestorId, LocalDateTime timeAdded) {
		return new WorkOrderEntity(requestorId, timeAdded);
	}

}
