package com.ubn.devops.ubnncsintegration.service;

import com.ubn.devops.ubnncsintegration.model.PaymentEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentQuery;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;

public interface PaymentService {
	
	public PaymentEntity savePaymentDetails(EPaymentConfirmation paymentConirmation);
	
	//public void confirmPaymentDetails(String formMNumber);

	public ApiResponse requestPayment(EPaymentConfirmation ePaymentConfirmation);
	
	public void queryTransaction(EPaymentQuery query); 
}
