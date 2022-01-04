package com.acme.workorderapi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.acme.workorderapi.utils.TestUtils;

@DataJpaTest
class WorkOrderRepositoryTest {

	@Autowired
	WorkOrderRepository woRepository;
	
	@Autowired
	TestEntityManager tem;
	
	@Test
	void getNextHighestPriorityWorkOrder_whenEmpty_thenShouldReturnNull() {
		WorkOrderEntity actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual).isNull();
	}
	
	@Test
	void getNextHighestPriorityWorkOrder_whenNormalRequests_thenShouldReturnHighestPriorityWorkOrder() {
		LocalDateTime currentTime = LocalDateTime.now();
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(1, currentTime.minusSeconds(50)));
		tem.clear();
		
		WorkOrderEntity actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(1);
		assertThat(actual.getRank()).isEqualTo(50);
		
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(2, currentTime.minusSeconds(100)));
		tem.clear();
		
		actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(2);
		assertThat(actual.getRank()).isEqualTo(100);
	}
	
	@Test
	void getNextHighestPriorityWorkOrder_whenPriorityRequests_thenShouldReturnHighestPriorityWorkOrder() {
		LocalDateTime currentTime = LocalDateTime.now();
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(3, currentTime));
		tem.clear();
		
		WorkOrderEntity actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(3);
		assertThat(actual.getRank()).isEqualTo(3);
		
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(6, currentTime.minusSeconds(100)));
		tem.clear();
		
		actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(6);
		assertThat(actual.getRank()).isEqualTo(100*Math.log(100));
	}	
	
	@Test
	void getNextHighestPriorityWorkOrder_whenVIPRequests_thenShouldReturnHighestPriorityVIPWorkOrder() {
		LocalDateTime currentTime = LocalDateTime.now();
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(5, currentTime));
		tem.clear();
		
		WorkOrderEntity actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(5);
		assertThat(actual.getRank()).isEqualTo(4);
		
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(10, currentTime.minusSeconds(100)));
		tem.clear();
		
		actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(10);
		assertThat(actual.getRank()).isEqualTo(2*100*Math.log(100));
	}
	
	@Test
	void getNextHighestPriorityWorkOrder_whenManagementOverrideAdded_thenShouldReturnManagementOverideWorkOrder() {
		LocalDateTime currentTime = LocalDateTime.now();
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(10, currentTime.minusSeconds(100)));
		tem.clear();
		
		WorkOrderEntity actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(10);
		assertThat(actual.getRank()).isEqualTo(2*100*Math.log(100));
		
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(15, currentTime.minusSeconds(100)));
		tem.clear();
		
		actual = this.woRepository.getHighestPriorityWorkOrder();
		assertThat(actual.getId()).isEqualTo(15);
		assertThat(actual.getRank()).isEqualTo(100);		
	}
	
	@Test
	void getPrioritisedWorkOrders_whenNotEmpty_thenShouldReturnPrioritisedList() {
		LocalDateTime timeAdded = LocalDateTime.now();

		// Normal requests
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(1, timeAdded));
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(2, timeAdded.minusSeconds(50)));

		// Priority requests
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(3, timeAdded.minusSeconds(50)));
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(6, timeAdded.minusSeconds(100)));
		
		// VIP requests
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(5, timeAdded.minusSeconds(100)));
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(10, timeAdded.minusSeconds(200)));
		
		// Management requests
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(15, timeAdded));
		tem.persistAndFlush(TestUtils.createWorkOrderEntity(30, timeAdded.minusSeconds(100)));
		tem.clear();
		
		Collection<WorkOrderEntity> prioritisedWorkOrders = this.woRepository.getPrioritisedWorkOrders();
		assertThat(prioritisedWorkOrders).isNotEmpty();
		
		List<Long> prioritisedWorkOrderIds = prioritisedWorkOrders.stream().map(x -> x.getId()).collect(Collectors.toList());
 		assertThat(prioritisedWorkOrderIds).containsExactly(30L, 15L, 10L, 5L, 6L, 3L, 2L, 1L);
	}
	
	@Test
	void getPrioritisedWorkOrders_whenEmpty_thenShouldReturnEmptyList() {
		Collection<WorkOrderEntity> prioritisedWorkOrders = this.woRepository.getPrioritisedWorkOrders();
		assertThat(prioritisedWorkOrders).isEmpty();
	}
	
	@Test
	void getAverageWaitTime() {
		LocalDateTime currentTime = LocalDateTime.now();
		Long avgWaitTime = this.woRepository.getAverageWaitTime(currentTime);
		assertThat(avgWaitTime).isZero();
		
		WorkOrderEntity workOrderToBePersisted = TestUtils.createWorkOrderEntity(1, currentTime.minusSeconds(200));
		tem.persistAndFlush(workOrderToBePersisted);
		avgWaitTime = this.woRepository.getAverageWaitTime(currentTime);
		assertThat(avgWaitTime).isBetween(200L, 201L);

		workOrderToBePersisted = TestUtils.createWorkOrderEntity(2, currentTime.minusSeconds(400));
		tem.persistAndFlush(workOrderToBePersisted);
		avgWaitTime = this.woRepository.getAverageWaitTime(currentTime);
		assertThat(avgWaitTime).isBetween(300L, 301L);	
	}
	
	@Test
	void getWorkOrderById_whenEmpty_shouldReturnEmptyOptional() {
		Optional<WorkOrderEntity> actual = this.woRepository.getWorkOrderById(1L);
		assertThat(actual).isEmpty();
	}

	@Test
	void getWorkOrderById_whenNotEmpty_shouldReturnOptionalWithWorkOrder() {
		LocalDateTime currentTime = LocalDateTime.now();
		WorkOrderEntity workOrderToBePersisted = TestUtils.createWorkOrderEntity(1, currentTime.minusSeconds(200));
		tem.persistAndFlush(workOrderToBePersisted);
		
		workOrderToBePersisted = TestUtils.createWorkOrderEntity(2, currentTime.minusSeconds(400));
		tem.persistAndFlush(workOrderToBePersisted);
		tem.clear();
		
		Optional<WorkOrderEntity> actual = this.woRepository.getWorkOrderById(1L);
		assertThat(actual).isPresent();
		assertThat(actual.get().getId()).isEqualTo(1L);
		assertThat(actual.get().getPosition()).isEqualTo(1);		

		actual = this.woRepository.getWorkOrderById(2L);
		assertThat(actual).isPresent();
		assertThat(actual.get().getId()).isEqualTo(2L);
		assertThat(actual.get().getPosition()).isZero();
	}
}
