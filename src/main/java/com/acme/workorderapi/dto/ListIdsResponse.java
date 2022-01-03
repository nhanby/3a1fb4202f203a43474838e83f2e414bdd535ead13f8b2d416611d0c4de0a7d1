package com.acme.workorderapi.dto;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListIdsResponse {
	private Collection<Long> workOrderIds;
}
