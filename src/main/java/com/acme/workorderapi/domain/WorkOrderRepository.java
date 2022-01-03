package com.acme.workorderapi.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkOrderRepository extends JpaRepository<WorkOrderEntity, Long> {

	WorkOrderEntity getHighestPriorityWorkOrder();
	
	Collection<WorkOrderEntity> getPrioritisedWorkOrders();
	
	Optional<WorkOrderEntity> getWorkOrderById(Long id);
	
	@Query(value = "SELECT ISNULL(AVG(TIMESTAMPDIFF(SECOND, TIME_ADDED, :currentTime)),0) AS mean_time_in_queue FROM WORKORDER",
		   nativeQuery = true)
	Long getAverageWaitTime(LocalDateTime currentTime);

}