package com.avaldes.model;

public class StatusMessage {
	private int status;
	private String message;

	public StatusMessage(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
}
