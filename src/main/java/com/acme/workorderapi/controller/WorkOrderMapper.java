package com.acme.workorderapi.controller;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.acme.workorderapi.domain.WorkOrderEntity;
import com.acme.workorderapi.dto.WorkOrderRequest;
import com.acme.workorderapi.dto.WorkOrderResponse;

public class WorkOrderMapper {
	
	private WorkOrderMapper() {}

	public static WorkOrderResponse mapToWorkOrderResponse(WorkOrderEntity woe) {
		return new WorkOrderResponse(woe.getId(), woe.getTimeAdded(), woe.getClassification(), woe.getRank());
	}
	
	public static WorkOrderEntity mapToWorkOrderEntity(WorkOrderRequest request) {
		return new WorkOrderEntity(request.getRequestorId(), request.getTimeAdded());
	}
	
	public static String getBaseURL() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
	}
}