package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubn.devops.ubnncsintegration.model.PaymentEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentQuery;
import com.ubn.devops.ubnncsintegration.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	/*
	 * @Autowired private EAssessmentNoticeService noticeService;
	 */
	@PostMapping("/payment-request")
	public ResponseEntity<PaymentEntity> requestPayment(@RequestBody EPaymentConfirmation ePaymentConfirmation){
		PaymentEntity entity = paymentService.savePaymentDetails(ePaymentConfirmation);
		
		return new ResponseEntity<PaymentEntity>(entity,HttpStatus.OK);
	}
		
	@PostMapping("/send-query-request")
	public void sendQueryRequest(@RequestBody EPaymentQuery query ){
		paymentService.queryTransaction(query);
	}

}
