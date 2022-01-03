package com.acme.workorderapi.service;

import java.time.LocalDateTime;
import java.util.Collection;

import com.acme.workorderapi.domain.WorkOrderEntity;

public interface WorkOrderService {

	WorkOrderEntity dequeue();

	WorkOrderEntity enqueue(WorkOrderEntity workOrderToEnqueue);

	Collection<Long> listIds();

	int getPositionById(Long id);

	void deleteAll();

	WorkOrderEntity getById(Long id);

	void deleteById(Long id);
	
	Long getAverageWaitTime(LocalDateTime currentTime);

}