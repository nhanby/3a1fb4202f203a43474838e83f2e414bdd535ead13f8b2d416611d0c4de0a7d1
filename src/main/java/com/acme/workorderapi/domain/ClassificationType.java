package com.acme.workorderapi.domain;

public enum ClassificationType {
	
	NORMAL(1), PRIORITY(2), VIP(3), MANAGEMENT(4);
	
	private int value;
	
	private ClassificationType(int value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return this.value;
	}

}