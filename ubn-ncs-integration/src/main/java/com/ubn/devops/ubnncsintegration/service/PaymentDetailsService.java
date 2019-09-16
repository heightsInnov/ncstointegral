package com.ubn.devops.ubnncsintegration.service;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;

public interface PaymentDetailsService {
	
	public PaymentDetails savePaymentDetails(EAssessmentNotice assessmentNotice);
	
	public ApiResponse fetchPaymentDetails(PaymentProcessRequest request);
	
	public void acknowledgePaymentDetails(String formMNumber);
	
	public ApiResponse processPayment(PaymentProcessRequest paymentProcessRequest);
	
	//public ApiResponse queryPaymentDetails(EPaymentQuery paymentQuery);
	
	public int updatePaymentWithNCSResponse(TransactionResponse response);

	/*
	 
	 * public void queryTransaction(EPaymentQuery query);
	 * 
	 * 
	 */
}


