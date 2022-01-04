package com.acme.workorderapi.controller;

import java.net.URI;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.acme.workorderapi.AppConstants;
import com.acme.workorderapi.domain.WorkOrderEntity;
import com.acme.workorderapi.dto.AverageWaitTimeResponse;
import com.acme.workorderapi.dto.ListIdsResponse;
import com.acme.workorderapi.dto.QueuePositionResponse;
import com.acme.workorderapi.dto.WorkOrderRequest;
import com.acme.workorderapi.dto.WorkOrderResponse;
import com.acme.workorderapi.service.WorkOrderService;

@RestController
@RequestMapping(value = AppConstants.WORK_ORDERS_API_BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkOrdersController {

	private WorkOrderService workOrdersService;

	public WorkOrdersController(WorkOrderService workOrderService) {
		this.workOrdersService = workOrderService;
	}

	@PostMapping("dequeue")
	public ResponseEntity<WorkOrderResponse> dequeue() {
		WorkOrderEntity dequeuedWorkEntity = this.workOrdersService.dequeue();
		return ResponseEntity.ok(WorkOrderMapper.mapToWorkOrderResponse(dequeuedWorkEntity));
	}

	@PostMapping(value = "enqueue", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WorkOrderResponse> enqueue(@Valid @RequestBody WorkOrderRequest request) {
		WorkOrderEntity workOrderToEnqueue = WorkOrderMapper.mapToWorkOrderEntity(request);
		WorkOrderEntity enqueuedWorkOrder = this.workOrdersService.enqueue(workOrderToEnqueue);
		WorkOrderResponse response = WorkOrderMapper.mapToWorkOrderResponse(enqueuedWorkOrder);
		String workOrderResourceURL = WorkOrderMapper.getBaseURL() + AppConstants.WORK_ORDERS_API_BASE_PATH
				+ enqueuedWorkOrder.getId();
		return ResponseEntity.created(URI.create(workOrderResourceURL)).body(response);
	}

	@GetMapping("listids")
	public ListIdsResponse listIds() {
		return new ListIdsResponse(this.workOrdersService.listIds());
	}

	@GetMapping("position/{id}")
	public QueuePositionResponse getQueuePosition(@PathVariable Long id) {
		return new QueuePositionResponse(this.workOrdersService.getPositionById(id));
	}

	@GetMapping("{id}")
	public WorkOrderResponse getById(@PathVariable Long id) {
		WorkOrderEntity retrievedWorkOrder = this.workOrdersService.getById(id);
		return WorkOrderMapper.mapToWorkOrderResponse(retrievedWorkOrder);
	}

	@GetMapping("/avgWaitTime/{currentTime}")
	public AverageWaitTimeResponse getAverageWaitTime(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime currentTime) {
		Long averageWaitTime = this.workOrdersService.getAverageWaitTime(currentTime);
		return new AverageWaitTimeResponse(averageWaitTime);
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable Long id) {
		this.workOrdersService.deleteById(id);
	}

	@DeleteMapping()
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAll() {
		this.workOrdersService.deleteAll();
	}



}