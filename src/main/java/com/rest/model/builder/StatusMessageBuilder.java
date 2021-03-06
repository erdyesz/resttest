package com.rest.model.builder;

import com.rest.model.StatusMessage;


public class StatusMessageBuilder {
	int status;
	String message;

	public StatusMessageBuilder message(String message) {
		this.message = message;
		return this;
	}
	
	public StatusMessageBuilder status(int status) {
		this.status = status;
		return this;
	}
	
	public StatusMessage build() {
		return new StatusMessage(status, message);
	}

}
