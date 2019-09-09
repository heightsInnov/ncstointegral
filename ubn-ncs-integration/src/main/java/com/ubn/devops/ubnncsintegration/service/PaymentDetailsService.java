package com.ubn.devops.ubnncsintegration.service;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;

public interface PaymentDetailsService {
	
	public PaymentDetails savePaymentDetails(EAssessmentNotice assessmentNotice);
	
	public ApiResponse fetchPaymentDetails(String formMNumber);
	
	public void acknowledgePaymentDetails(String formMNumber);
	
	public ApiResponse processPayment(PaymentProcessRequest paymentProcessRequest);
	
	/*
	 
	 * public void queryTransaction(EPaymentQuery query);
	 * 
	 * 
	 */
}


