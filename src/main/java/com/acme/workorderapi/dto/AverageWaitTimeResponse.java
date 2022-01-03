package com.acme.workorderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AverageWaitTimeResponse {
	private Long averageWaitTime;
}
