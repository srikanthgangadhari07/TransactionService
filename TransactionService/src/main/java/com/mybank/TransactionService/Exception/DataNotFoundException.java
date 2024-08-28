package com.mybank.TransactionService.Exception;

@SuppressWarnings("serial")
public class DataNotFoundException extends RuntimeException {
	private String message;
	private String description;
	
	public DataNotFoundException(String message, String description) {
		super();
		this.message = message;
		this.description = description;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
