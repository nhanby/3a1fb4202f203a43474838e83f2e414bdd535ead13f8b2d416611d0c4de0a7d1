package com.acme.workorderapi.dto;

import java.time.LocalDateTime;

import com.acme.workorderapi.domain.ClassificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class WorkOrderResponse {
	private Long requestorId;
	private LocalDateTime timeAdded;
	private ClassificationType type;
	private Double rank;
}