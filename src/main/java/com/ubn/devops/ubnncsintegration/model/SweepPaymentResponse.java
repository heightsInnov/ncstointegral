package com.ubn.devops.ubnncsintegration.model;

public class SweepPaymentResponse {

	private String update_response;
	private String miserve_response;
	
	public SweepPaymentResponse() {
	}
	public SweepPaymentResponse(String update_response, String miserve_response) {
		this.update_response = update_response;
		this.miserve_response = miserve_response;
	}
	public String getUpdate_response() {
		return update_response;
	}
	public void setUpdate_response(String update_response) {
		this.update_response = update_response;
	}
	public String getMiserve_response() {
		return miserve_response;
	}
	public void setMiserve_response(String miserve_response) {
		this.miserve_response = miserve_response;
	}
}
