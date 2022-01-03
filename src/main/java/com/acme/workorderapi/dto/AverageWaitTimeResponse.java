package com.acme.workorderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AverageWaitTimeResponse {
	private Long averageWaitTime;
}
