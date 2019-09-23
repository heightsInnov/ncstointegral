package com.ubn.devops.ubnncsintegration.response;

public class ApiResponse {
	
	public static final int SERVER_ERROR=500;
	public static final int NOT_FOUND=404;
	public static final int SUCCESSFUL=200;
	
	public static final int ALREADY_PAID = 409;
	public static final int ALREADY_EXIST=422;
	public static final int INVALID_REFERENCE=208;

	private int code;
	private String message;
	private Object body;

	public ApiResponse() {
		
	}
	public ApiResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(Object body) {
		this.body = body;
	}
}
