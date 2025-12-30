package com.v2.competency.management.entities;

public enum WorkflowSessionStatus {
	
	
NOT_STARTED("NOT STARTED"), IN_PROGRESS("IN PROGRESS"), COMPLETE("COMPLETE");
	
	String status;
	
	private WorkflowSessionStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
