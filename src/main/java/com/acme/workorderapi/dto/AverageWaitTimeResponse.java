package com.acme.workorderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AverageWaitTimeResponse {
	private Long averageWaitTime;
}