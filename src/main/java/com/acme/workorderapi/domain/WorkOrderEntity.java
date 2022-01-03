package com.acme.workorderapi.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "WORKORDER")
@Data
@EqualsAndHashCode
@ToString
@NamedNativeQuery(name = "WorkOrderEntity.getHighestPriorityWorkOrder", query = WorkOrderEntity.GET_HIGHEST_PRIORITY_WORK_ORDER_SQL, resultClass = WorkOrderEntity.class)
@NamedNativeQuery(name = "WorkOrderEntity.getPrioritisedWorkOrders", query = WorkOrderEntity.GET_PRIORITISED_WORK_ORDERS_SQL, resultClass = WorkOrderEntity.class)
@NamedNativeQuery(name = "WorkOrderEntity.getWorkOrderById", query = WorkOrderEntity.GET_WORK_ORDER_SQL, resultClass = WorkOrderEntity.class)
public class WorkOrderEntity {
	public static final String GET_PRIORITISED_WORK_ORDERS_SQL = 
			"SELECT rownum() -1 AS position, * FROM ("
			+ "  (SELECT id, classification, time_added, TIMESTAMPDIFF(SECOND, TIME_ADDED, NOW()) AS rank "
			+ "    FROM workorder "
			+ "    WHERE classification = 4 ORDER BY rank DESC) "
			+ "  UNION ALL " 
			+ "  (SELECT t.id, t.classification, t.time_added, " 
			+ "     CASE "
			+ "       WHEN t.classification = 1 THEN t.time_in_queue "
			+ "       WHEN t.classification = 2 THEN greatest(3, t.time_in_queue * LN(greatest(1, t.time_in_queue))) "
			+ "       WHEN t.classification = 3 THEN greatest(4, 2 * t.time_in_queue * LN(greatest(1, t.time_in_queue))) "
			+ "       ELSE t.time_in_queue " 
			+ "     END as rank "
			+ "    FROM (SELECT id, classification, time_added, TIMESTAMPDIFF(SECOND, TIME_ADDED, NOW()) AS time_in_queue "
			+ "           FROM workorder "
			+ "           WHERE classification <> 4 ) t "
			+ "    ORDER BY rank DESC)"
			+ ")";
	public static final String GET_HIGHEST_PRIORITY_WORK_ORDER_SQL = GET_PRIORITISED_WORK_ORDERS_SQL + " LIMIT 1";
	public static final String GET_WORK_ORDER_SQL = "SELECT * FROM (" + GET_PRIORITISED_WORK_ORDERS_SQL + ") WHERE id = :id";
	
	@Id
	private Long id;
	private LocalDateTime timeAdded;
	private ClassificationType classification;
	private Integer position;
	private Double rank;

	protected WorkOrderEntity() {
	}

	public WorkOrderEntity(Long id, LocalDateTime timeAdded) {
		this.id = id;
		this.timeAdded = timeAdded;
		this.classification = getClassificationType(id);
	}

	private ClassificationType getClassificationType(Long id) {
		boolean isDivisibleBy5 = id % 5 == 0;
		boolean isDivisibleBy3 = id % 3 == 0;
		if (isDivisibleBy5 && isDivisibleBy3)
			return ClassificationType.MANAGEMENT;
		else if (isDivisibleBy5)
			return ClassificationType.VIP;
		else if (isDivisibleBy3)
			return ClassificationType.PRIORITY;
		else
			return ClassificationType.NORMAL;
	}

}
