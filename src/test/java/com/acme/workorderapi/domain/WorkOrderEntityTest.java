package com.acme.workorderapi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.acme.workorderapi.utils.TestUtils;

@DataJpaTest
class WorkOrderEntityTest {

	@Autowired
	TestEntityManager tem;

	@Test
	void testCreation() {
		LocalDateTime creationTime = LocalDateTime.now();
		WorkOrderEntity workEntity1 = TestUtils.createWorkOrderEntity(1, creationTime);
		assertThat(workEntity1.getId()).isEqualTo(1);
		assertThat(workEntity1.getTimeAdded()).isEqualTo(creationTime);
	}

	@Test
	void testEquals() {
		LocalDateTime creationTime = LocalDateTime.now();
		
		WorkOrderEntity workEntity1 = TestUtils.createWorkOrderEntity(1, creationTime);
		WorkOrderEntity workEntity2 = TestUtils.createWorkOrderEntity(1, creationTime);
		WorkOrderEntity workEntity3 = TestUtils.createWorkOrderEntity(2, creationTime);

		assertThat(workEntity1).isEqualTo(workEntity1).isEqualTo(workEntity2);
		assertThat(workEntity2).isNotEqualTo(workEntity3);
	}

	@Test
	void saveShouldPersistData() {
		WorkOrderEntity workOrderToBePersisted = TestUtils.createWorkOrderEntity(1, LocalDateTime.now());
		WorkOrderEntity persistedEntity = this.tem.persistFlushFind(workOrderToBePersisted);

		assertThat(persistedEntity).isEqualTo(workOrderToBePersisted);
	}
}
