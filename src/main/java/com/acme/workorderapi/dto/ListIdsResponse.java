package com.acme.workorderapi.dto;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ListIdsResponse {
	private Collection<Long> workOrderIds;
}
