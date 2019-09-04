package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentQuery;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;
	

	@PostMapping("/payment-request")
	public ResponseEntity<PaymentDetails> requestPayment(@RequestBody EPaymentConfirmation ePaymentConfirmation){
		PaymentDetails entity = paymentDetailsService.updatePaymentDetailsWithRequest(ePaymentConfirmation);
		return new ResponseEntity<PaymentDetails>(entity,HttpStatus.OK);
	}
		
	@GetMapping("/details")
	public ApiResponse getPaymentDetails(@RequestParam String declarantCode){
		return paymentDetailsService.fetchPaymentDetails(declarantCode);
	}
	
	@PostMapping("/send-query-request")
	public void sendQueryRequest(@RequestBody EPaymentQuery query ){
		//paymentDetailsService.queryTransaction(query);
	}

}
