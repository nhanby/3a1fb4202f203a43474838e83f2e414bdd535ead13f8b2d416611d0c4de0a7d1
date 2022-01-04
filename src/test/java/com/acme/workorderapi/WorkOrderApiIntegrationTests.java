package com.acme.workorderapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.acme.workorderapi.dto.AverageWaitTimeResponse;
import com.acme.workorderapi.dto.ListIdsResponse;
import com.acme.workorderapi.dto.QueuePositionResponse;
import com.acme.workorderapi.dto.WorkOrderRequest;
import com.acme.workorderapi.dto.WorkOrderResponse;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class WorkOrderApiIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeAll
	public static void setUp() {
		System.setProperty("javax.net.ssl.trustStore",
				WorkOrderApiIntegrationTests.class.getClassLoader().getResource("trust.jks").getFile());
		System.setProperty("javax.net.ssl.trustStorePassword", "password");
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
	}

	@AfterEach
	void after() {
		restTemplate.delete(TestConstants.WORK_ORDERS_API_BASE_PATH);
	}

	@Test
	void enqueueWorkOrder_shouldReturnCreatedStatusAndURILocationOfNewlyCreatedWorkOrder() {
		ResponseEntity<WorkOrderResponse> createWorkOrderResponse = createWorkOrder("1");
		assertThat(createWorkOrderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		URI workOrderURI = createWorkOrderResponse.getHeaders().getLocation();
		assertThat(workOrderURI).isNotNull();

		ResponseEntity<WorkOrderResponse> getWorkOrderResponse = restTemplate.getForEntity(workOrderURI,
				WorkOrderResponse.class);

		WorkOrderResponse retrievedWorkOrder = getWorkOrderResponse.getBody();
		WorkOrderResponse createdWorkOrder = createWorkOrderResponse.getBody();

		assertThat(getWorkOrderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(retrievedWorkOrder.getRequestorId()).isEqualTo(createdWorkOrder.getRequestorId());
		assertThat(retrievedWorkOrder.getTimeAdded()).isEqualTo(createdWorkOrder.getTimeAdded());
		assertThat(retrievedWorkOrder.getType()).isEqualTo(createdWorkOrder.getType());
	}

	@Test
	void dequeueWorkOrder_whenNonEmptyQueue_shouldReturnNextHighestPriorityWorkOrder() {
		createWorkOrder("1");
		createWorkOrder("2");
		ResponseEntity<WorkOrderResponse> expectedResponse = createWorkOrder("3");

		ResponseEntity<WorkOrderResponse> actualResponse = restTemplate.postForEntity(TestConstants.DEQUEUE_URL, null,
				WorkOrderResponse.class);

		assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(actualResponse.getBody().getRequestorId()).isEqualTo(expectedResponse.getBody().getRequestorId());
	}

	@Test
	void listWorkOrderIds_shouldReturnPrioritisedListOfIds() {
		createWorkOrder("1");
		createWorkOrder("2");
		createWorkOrder("3");
		createWorkOrder("4");
		createWorkOrder("5");
		createWorkOrder("6");
		createWorkOrder("15");
		createWorkOrder("30");

		ResponseEntity<ListIdsResponse> response = restTemplate.getForEntity(TestConstants.LIST_IDS_URL,
				ListIdsResponse.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getWorkOrderIds()).isNotEmpty().containsExactly(15L, 30L, 5L, 3L, 6L, 1L, 2L, 4L);
	}

	@Test
	void getPositionById_shouldReturnPositionOfWorkOrder() {
		createWorkOrder("1");
		createWorkOrder("2");
		createWorkOrder("3");
		createWorkOrder("4");
		createWorkOrder("5");
		createWorkOrder("6");
		createWorkOrder("15");
		createWorkOrder("30");

		ResponseEntity<QueuePositionResponse> response = restTemplate.getForEntity(TestConstants.POSITION_URL,
				QueuePositionResponse.class, 4);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getPosition()).isEqualTo(7);

		response = restTemplate.getForEntity(TestConstants.POSITION_URL, QueuePositionResponse.class, 1);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getPosition()).isEqualTo(5);

		response = restTemplate.getForEntity(TestConstants.POSITION_URL, QueuePositionResponse.class, 30);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getPosition()).isEqualTo(1);

		response = restTemplate.getForEntity(TestConstants.POSITION_URL, QueuePositionResponse.class, 15);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getPosition()).isZero();

		response = restTemplate.getForEntity(TestConstants.POSITION_URL, QueuePositionResponse.class, 50);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteWorkOrderById_whenWorkOrderExists_shouldReturnNoContentAndDeleteWorkOrder() {
		createWorkOrder("5");

		ResponseEntity<WorkOrderResponse> getWorkOrderResponse = restTemplate.getForEntity(TestConstants.WORK_ORDER_URL,
				WorkOrderResponse.class, 5);

		assertThat(getWorkOrderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getWorkOrderResponse.getBody().getRequestorId()).isEqualTo(5L);

		ResponseEntity<Void> deleteWorkOrderResponse = restTemplate.exchange(TestConstants.WORK_ORDER_URL,
				HttpMethod.DELETE, null, Void.class, 5);

		assertThat(deleteWorkOrderResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		getWorkOrderResponse = restTemplate.getForEntity(TestConstants.WORK_ORDER_URL, WorkOrderResponse.class, 5);

		assertThat(getWorkOrderResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void getAverageWaitTime_whenEmptyQueue_shouldReturnOkAndZeroAverageWaitTime() {
		String currentTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

		ResponseEntity<AverageWaitTimeResponse> averageWaitTimeResponse = restTemplate
				.getForEntity(TestConstants.GET_AVERAGE_WAIT_TIME_URL, AverageWaitTimeResponse.class, currentTimeStr);

		assertThat(averageWaitTimeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(averageWaitTimeResponse.getBody().getAverageWaitTime()).isZero();
	}

	@Test
	void getAverageWaitTime_whenNonEmptyQueue_shouldReturnOkAndNonZeroAverageWaitTime() {
		LocalDateTime currentTime = LocalDateTime.now();
		createWorkOrder("6", currentTime.minusSeconds(100));
		createWorkOrder("7", currentTime.minusSeconds(200));		

		String currentTimeStr = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		ResponseEntity<AverageWaitTimeResponse> averageWaitTimeResponse = restTemplate
				.getForEntity(TestConstants.GET_AVERAGE_WAIT_TIME_URL, AverageWaitTimeResponse.class, currentTimeStr);
		assertThat(averageWaitTimeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(averageWaitTimeResponse.getBody().getAverageWaitTime()).isEqualTo(150);
	}

	private ResponseEntity<WorkOrderResponse> createWorkOrder(String requestorId) {
		return createWorkOrder(requestorId, LocalDateTime.now());
	}

	private ResponseEntity<WorkOrderResponse> createWorkOrder(String requestorId, LocalDateTime timeAdded) {
		WorkOrderRequest request = new WorkOrderRequest(requestorId, timeAdded);
		return restTemplate.postForEntity(TestConstants.ENQUEUE_URL, request, WorkOrderResponse.class);
	}
}