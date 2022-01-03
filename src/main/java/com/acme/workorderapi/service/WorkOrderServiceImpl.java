package com.acme.workorderapi.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.acme.workorderapi.AppConstants;
import com.acme.workorderapi.domain.WorkOrderEntity;
import com.acme.workorderapi.domain.WorkOrderRepository;
import com.acme.workorderapi.exception.EmptyQueueException;
import com.acme.workorderapi.exception.WorkOrderAlreadyExistsException;
import com.acme.workorderapi.exception.WorkOrderNotFoundException;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {
	private WorkOrderRepository workOrderRepository;

	public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository) {
		this.workOrderRepository = workOrderRepository;
	}

	@Override
	public WorkOrderEntity dequeue() {
		WorkOrderEntity nextHighestPriorityWorkOrder = this.workOrderRepository.getHighestPriorityWorkOrder();
		if(nextHighestPriorityWorkOrder == null) {
			throw new EmptyQueueException(AppConstants.EMPTY_QUEUE_ERROR_MSG);
		}
		this.workOrderRepository.delete(nextHighestPriorityWorkOrder);
		return nextHighestPriorityWorkOrder;
	}

	@Override
	public WorkOrderEntity enqueue(WorkOrderEntity workOrderToEnqueue) {
		if(!this.workOrderRepository.existsById(workOrderToEnqueue.getId())) {
			return this.workOrderRepository.save(workOrderToEnqueue);			
		}
		
		throw new WorkOrderAlreadyExistsException();
	}

	@Override
	public Collection<Long> listIds() {
		return this.workOrderRepository.getPrioritisedWorkOrders()
				.stream()
				.map(WorkOrderEntity::getId)
				.collect(Collectors.toList());
	}
	
	@Override
	public int getPositionById(Long id) {
		return getById(id).getPosition();
	}

	public WorkOrderEntity getById(Long id) {
		Optional<WorkOrderEntity> requestedWorkOrder = this.workOrderRepository.getWorkOrderById(id);
		if(requestedWorkOrder.isEmpty()) {
			throw new WorkOrderNotFoundException();
		}
		return requestedWorkOrder.get();
	}
	
	public Long getAverageWaitTime(LocalDateTime currentTime) {
		return this.workOrderRepository.getAverageWaitTime(currentTime);
	}

	public void deleteById(Long id) {
		WorkOrderEntity workOrderToDelete = getById(id);
		this.workOrderRepository.delete(workOrderToDelete);
	}

	@Override
	public void deleteAll() {
		this.workOrderRepository.deleteAll();
	}

}