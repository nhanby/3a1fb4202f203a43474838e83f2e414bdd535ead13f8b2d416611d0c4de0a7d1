package com.acme.workorderapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.acme.workorderapi.AppConstants;
import com.acme.workorderapi.domain.WorkOrderEntity;
import com.acme.workorderapi.domain.WorkOrderRepository;
import com.acme.workorderapi.exception.EmptyQueueException;
import com.acme.workorderapi.exception.WorkOrderNotFoundException;
import com.acme.workorderapi.utils.TestUtils;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceImplTest {

	@Mock
	private WorkOrderRepository workOrderRepository;

	@InjectMocks
	private WorkOrderServiceImpl workOrderService;

	@Test
	void dequeueWorkOrder_whenNonEmpty_returnsAndDeletesHighestPriorityWorkOrder() {
		WorkOrderEntity expectedWorkOrder = TestUtils.createWorkOrderEntity(1L, LocalDateTime.now());
		given(workOrderRepository.getHighestPriorityWorkOrder()).willReturn(expectedWorkOrder);

		WorkOrderEntity dequeuedWorkOrder = this.workOrderService.dequeue();

		assertThat(dequeuedWorkOrder.getId()).isEqualTo(expectedWorkOrder.getId());
		assertThat(dequeuedWorkOrder.getTimeAdded()).isEqualTo(expectedWorkOrder.getTimeAdded());
		verify(workOrderRepository).delete(expectedWorkOrder);
	}

	@Test
	void dequeueWorkOrder_whenEmpty_returnsAndDeletesHighestPriorityWorkOrder() {
		given(workOrderRepository.getHighestPriorityWorkOrder()).willReturn(null);

		assertThatThrownBy(() -> this.workOrderService.dequeue())
		   .isInstanceOf(EmptyQueueException.class)
		   .hasMessage(AppConstants.EMPTY_QUEUE_ERROR_MSG);
	}

	@Test
	void enqueueWorkOrder_returnsNewlyAddedWorkOrder() {
		WorkOrderEntity workOrderToEnqueue = TestUtils.createWorkOrderEntity(1L, LocalDateTime.now());
		given(workOrderRepository.save(Mockito.any(WorkOrderEntity.class))).willReturn(workOrderToEnqueue);
		WorkOrderEntity enqueuedWorkOrder = this.workOrderService.enqueue(workOrderToEnqueue);
		assertThat(enqueuedWorkOrder).isEqualTo(workOrderToEnqueue);
	}

	@Test
	void listIds_whenNotEmpty_returnsPrioritisedListOfWorkOrderIds() {
		WorkOrderEntity expectedWorkOrder1 = TestUtils.createWorkOrderEntity(1L, LocalDateTime.now());
		WorkOrderEntity expectedWorkOrder2 = TestUtils.createWorkOrderEntity(2L, LocalDateTime.now());

		given(workOrderRepository.getPrioritisedWorkOrders()).willReturn(Arrays.asList(expectedWorkOrder1, expectedWorkOrder2));
		Collection<Long> workOrderIds = this.workOrderService.listIds();
		assertThat(workOrderIds)
		     .isNotNull()
		     .isNotEmpty()
		     .hasSize(2)
		     .contains(1L)
		     .contains(2L);
	}
	
	@Test
	void listIds_whenEmpty_returnsPrioritisedListOfWorkOrderIds() {
		given(workOrderRepository.getPrioritisedWorkOrders()).willReturn(Collections.emptyList());
		Collection<Long> workOrderIds = this.workOrderService.listIds();
		assertThat(workOrderIds)
		     .isEmpty();
	}
	
	@Test
	void getById_whenExists_shouldReturnRetrievedWorkOrder() {
		WorkOrderEntity expectedWorkOrder = TestUtils.createWorkOrderEntity(1,LocalDateTime.now());
		given(workOrderRepository.getWorkOrderById(1L)).willReturn(Optional.of(expectedWorkOrder));
		WorkOrderEntity retrievedWorkOrder = this.workOrderService.getById(1L);
		assertThat(retrievedWorkOrder).isEqualTo(expectedWorkOrder);		
	}

	@Test
	void getById_whenNotExists_shouldThrowWorkOrderNotFoundException() {
		given(workOrderRepository.getWorkOrderById(1L)).willReturn(Optional.empty());
		assertThatThrownBy(() -> {
			this.workOrderService.getById(1L);
		}).isInstanceOf(WorkOrderNotFoundException.class);
	}
	
	@Test
	void getPositionById_whenExists_shouldReturnRetrievedWorkOrder() {
		WorkOrderEntity expectedWorkOrder = TestUtils.createWorkOrderEntity(1,LocalDateTime.now(), 2);
		given(workOrderRepository.getWorkOrderById(1L)).willReturn(Optional.of(expectedWorkOrder));
		int position = this.workOrderService.getPositionById(1L);
		assertThat(position).isEqualTo(2);		
	}

	@Test
	void getPositionById_whenNotExists_shouldThrowWorkOrderNotFoundException() {
		given(workOrderRepository.getWorkOrderById(1L)).willReturn(Optional.empty());
		assertThatThrownBy(() -> {
			this.workOrderService.getPositionById(1L);
		}).isInstanceOf(WorkOrderNotFoundException.class);
	}
	
	@Test
	void deleteById_whenExists_shouldDeleteWorkOrder() {
		WorkOrderEntity workOrderToDelete = TestUtils.createWorkOrderEntity(1,LocalDateTime.now());
		given(workOrderRepository.getWorkOrderById(1L)).willReturn(Optional.of(workOrderToDelete));
		this.workOrderService.deleteById(1L);
		verify(workOrderRepository, times(1)).delete(workOrderToDelete);
	}

	@Test
	void deleteById_whenNotExists_shouldThrowWorkOrderNotFoundException() {
		given(workOrderRepository.getWorkOrderById(1L)).willReturn(Optional.empty());
		assertThatThrownBy(() -> {
			this.workOrderService.deleteById(1L);
		}).isInstanceOf(WorkOrderNotFoundException.class);
	}
	
	@Test
	void getAverageWaitTime() {
		LocalDateTime currentTime = LocalDateTime.now();
		given(workOrderRepository.getAverageWaitTime(any(LocalDateTime.class))).willReturn(100L);
		
		Long averageWaitTime = this.workOrderService.getAverageWaitTime(currentTime);
		assertThat(averageWaitTime).isEqualTo(100);
		verify(workOrderRepository, times(1)).getAverageWaitTime(currentTime);
	}

}
