package com.acme.workorderapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.acme.workorderapi.AppConstants;
import com.acme.workorderapi.TestConstants;
import com.acme.workorderapi.domain.ClassificationType;
import com.acme.workorderapi.domain.WorkOrderEntity;
import com.acme.workorderapi.exception.WorkOrderAlreadyExistsException;
import com.acme.workorderapi.exception.WorkOrderNotFoundException;
import com.acme.workorderapi.service.WorkOrderService;
import com.acme.workorderapi.utils.TestUtils;

@WebMvcTest(WorkOrdersController.class)
class WorkOrdersControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	WorkOrderService workOrderQueueService;

	@Test
	void givenDequeue_whenEmptyQueue_thenReturnNotFound() throws Exception {
		given(workOrderQueueService.dequeue()).willThrow(new WorkOrderNotFoundException());

		mockMvc.perform(post(TestConstants.DEQUEUE_URL))
		.andExpect(status().isNotFound());
	}

	@Test
	void givenDequeue_whenNonEmptyQueue_thenReturnHighestPriorityWorkOrderWithHttpOk() throws Exception {
		WorkOrderEntity expected = TestUtils.createWorkOrderEntity(1, LocalDateTime.now());
		given(workOrderQueueService.dequeue()).willReturn(expected);

		mockMvc.perform(post(TestConstants.DEQUEUE_URL).contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().isOk())
		.andExpect(jsonPath("timeAdded").value(expected.getTimeAdded().toString().replaceAll("0$", "")));
	}

	@ParameterizedTest
    @CsvSource({
        "{}",
        "{ \"timeAdded\": \"1\" }",
        "{ \"requestorId\": \"1\" }",
        "{ \"requestorId\":\"0\", \"timeAdded\": 1 }",
        "{ \"requestorId\":\"9223372036854775808\", \"timeAdded\": 1 }",
        "{ \"requestorId\": \"3.14\", \"timeAdded\": 1 }"    
    })
	void givenEnqueue_whenInvalidRequest_thenHttpBadRequestReturned(String jsonPayload) throws Exception {
		mockMvc.perform(post(TestConstants.ENQUEUE_URL)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonPayload)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	void givenEnqueue_whenXmlPayload_thenUnsupportedMediaTypeReturned() throws Exception {	
		mockMvc.perform(post(TestConstants.ENQUEUE_URL)
				.accept(MediaType.APPLICATION_JSON)
				.content("<xml/>")
				.contentType(MediaType.APPLICATION_XML_VALUE))
		.andExpect(status().isUnsupportedMediaType());
	}
	
	@Test
	void givenEnqueue_whenResourceAlreadyExists_thenHttpConflictReturned() throws Exception {
		String jsonPayload = "{ \"requestorId\":\"1\", \"timeAdded\": \"2022-01-01T00:00:00\" }";
		
		given(workOrderQueueService.enqueue(any())).willThrow(new WorkOrderAlreadyExistsException());

		mockMvc.perform(post(TestConstants.ENQUEUE_URL)
				.accept(MediaType.APPLICATION_JSON)
				.content(jsonPayload)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isConflict());
	}

	@Test
	void givenEnqueue_whenValidRequest_thenReturnNewlyCreatedWorkOrderWithHttpCreatedStatus() throws Exception {
		String jsonPayload = "{ \"requestorId\":\"1\", \"timeAdded\": \"2022-01-01T00:00:00\" }";

		WorkOrderEntity expectedEntity = TestUtils.createWorkOrderEntity(1L, LocalDateTime.parse("2022-01-01T00:00:00"));
		given(workOrderQueueService.enqueue(any())).willReturn(expectedEntity);

		mockMvc.perform(post(TestConstants.ENQUEUE_URL)
				        	.accept(MediaType.APPLICATION_JSON)
				        	.contentType(MediaType.APPLICATION_JSON)
				        	.content(jsonPayload))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("requestorId").value(1))
		.andExpect(jsonPath("timeAdded").value("2022-01-01T00:00:00"))
		.andExpect(redirectedUrlPattern("http://*/"+TestConstants.WORK_ORDERS_API_BASE_PATH+"/1"));
	}

	@Test
	void givenListIds_whenEmpty_shouldReturnHttpOkAndEmptyList() throws Exception {
		given(workOrderQueueService.listIds()).willReturn(Collections.emptyList());

		mockMvc.perform(get(TestConstants.LIST_IDS_URL)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("workOrderIds").isEmpty());
	}

	@Test
	void givenListIds_whenNotEmpty_shouldReturnHttpOkAndListOfWorkOrderIds() throws Exception {
		given(workOrderQueueService.listIds()).willReturn(List.of(1L, 2L));

		mockMvc.perform(get(TestConstants.LIST_IDS_URL)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("workOrderIds").isNotEmpty())
		.andExpect(jsonPath("$.workOrderIds.length()").value(2));
	}
		
	@Test
	void givenQueuePosition_whenNotExists_shouldReturnNotFoundAndErrorMessage() throws Exception {
		given(workOrderQueueService.getPositionById(1L)).willThrow(new WorkOrderNotFoundException());

		mockMvc.perform(get(TestConstants.POSITION_URL, 1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("message").value(AppConstants.WORK_ORDER_NOT_FOUND_ERROR_MSG));
	}
	
	@Test
	void givenQueuePosition_whenExists_shouldReturnHttpOkAndQueuePositionOfWorkOrder() throws Exception {
		given(workOrderQueueService.getPositionById(1L)).willReturn(5);

		mockMvc.perform(get(TestConstants.POSITION_URL, 1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("position").value(5));
	}
	
	@Test
	void givenGetById_whenNotAcceptableMediaType_shouldReturnNotFoundAndErrorMessage() throws Exception {	
		mockMvc.perform(get(TestConstants.WORK_ORDER_URL, 1)
				.accept(MediaType.APPLICATION_XML_VALUE))
		.andExpect(status().isNotAcceptable());
	}
	
	@Test
	void givenGetById_whenNotExists_shouldReturnNotFoundAndErrorMessage() throws Exception {

		given(workOrderQueueService.getById(1L)).willThrow(new WorkOrderNotFoundException());

		mockMvc.perform(get(TestConstants.WORK_ORDER_URL, 1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("message").value(AppConstants.WORK_ORDER_NOT_FOUND_ERROR_MSG));
	}
	
	@Test
	void givenGetById_whenExists_shouldReturnOkAndWorkOrder() throws Exception {
		LocalDateTime timeAdded = LocalDateTime.now();
		WorkOrderEntity expected = TestUtils.createWorkOrderEntity(1, timeAdded);
		expected.setRank(1.0);
		given(workOrderQueueService.getById(1L)).willReturn(expected);

		mockMvc.perform(get(TestConstants.WORK_ORDER_URL, 1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("requestorId").value(1))
		.andExpect(jsonPath("timeAdded").value(timeAdded.toString()))
		.andExpect(jsonPath("type").value(ClassificationType.NORMAL.name()))
		.andExpect(jsonPath("rank").value(1.0));
	}
	
	@Test
	void givenDeleteById_whenNotExists_shouldReturnNotFoundAndErrorMessage() throws Exception {
		given(workOrderQueueService.getById(1L)).willThrow(new WorkOrderNotFoundException());

		mockMvc.perform(get(TestConstants.WORK_ORDER_URL, 1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("message").value(AppConstants.WORK_ORDER_NOT_FOUND_ERROR_MSG));
	}	

	@Test
	void givenDeleteById_whenExists_shouldReturnNoContent() throws Exception {
		WorkOrderEntity expected = TestUtils.createWorkOrderEntity(1, LocalDateTime.now());
		given(workOrderQueueService.getById(1L)).willReturn(expected);
		mockMvc.perform(delete(TestConstants.WORK_ORDER_URL, 1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}	
}