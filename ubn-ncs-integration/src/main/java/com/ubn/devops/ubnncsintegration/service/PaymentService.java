package com.ubn.devops.ubnncsintegration.service;

import com.ubn.devops.ubnncsintegration.model.PaymentModel;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;

public interface PaymentService {
	
	public PaymentModel savePaymentDetails(EAssessmentNotice assessmentNotice);
	
	public PaymentModel updatePaymentDetailsWithRequest(EPaymentConfirmation paymentConirmation);
	
	public void updatePaymentDetailsWithResponse(TransactionResponse response);

	public ApiResponse fetchPaymentDetails(String declarantCode);
	
	/*
	 
	 * public void queryTransaction(EPaymentQuery query);
	 * 
	 * 
	 */
}


