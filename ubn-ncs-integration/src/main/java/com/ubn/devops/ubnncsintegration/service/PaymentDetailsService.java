package com.ubn.devops.ubnncsintegration.service;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;

public interface PaymentDetailsService {
	
	public PaymentDetails savePaymentDetails(EAssessmentNotice assessmentNotice);
	
	public PaymentDetails updatePaymentDetailsWithRequest(EPaymentConfirmation paymentConirmation);
	
	public void updatePaymentDetailsWithResponse(TransactionResponse response);

	public ApiResponse fetchPaymentDetails(String declarantCode);
	
	public void acknowledgePaymentDetails(String declarantCode);
	
	public ApiResponse processPayment(PaymentProcessRequest paymentProcessRequest);
	
	/*
	 
	 * public void queryTransaction(EPaymentQuery query);
	 * 
	 * 
	 */
}


