package com.ubn.devops.ubnncsintegration.response;

public class StatusResponse {

	private String statusCode;

	private String message;

	public StatusResponse() {

	}

	public StatusResponse(String statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "StatusResponse [statusCode=" + statusCode + ", message=" + message + "]";
	}

}
