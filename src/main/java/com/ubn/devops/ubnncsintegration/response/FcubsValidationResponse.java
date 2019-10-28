package com.ubn.devops.ubnncsintegration.response;

public class FcubsValidationResponse {

	private int status;
	private String message;

	public FcubsValidationResponse() {

	}

	public FcubsValidationResponse(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "FcubsValidationResponse [status=" + status + ", message=" + message + "]";
	}
	
	
	

	

}
