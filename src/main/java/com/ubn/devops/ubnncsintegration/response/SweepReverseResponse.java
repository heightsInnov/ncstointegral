package com.ubn.devops.ubnncsintegration.response;

import com.ubn.devops.ubnncsintegration.model.AccountEnquiry;

public class SweepReverseResponse {

	private String code;
	private String message;
	private String body;
	private AccountEnquiry data;

	public SweepReverseResponse() {
		
	}
	
	public SweepReverseResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
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
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	public AccountEnquiry getData() {
		return data;
	}

	public void setData(AccountEnquiry data) {
		this.data = data;
	}
}
