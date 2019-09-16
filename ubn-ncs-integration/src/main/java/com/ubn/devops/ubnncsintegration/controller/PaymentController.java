package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentQuery;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	@PostMapping("/details")
	public ApiResponse getPaymentDetails(@RequestBody PaymentProcessRequest request){
		return paymentDetailsService.fetchPaymentDetails(request);
	}
	
	@PostMapping("/process-payment")
	public ApiResponse processPayment(@RequestBody PaymentProcessRequest request){
		return paymentDetailsService.processPayment(request);
	}
	
	@PostMapping("/query-payment")
	public ApiResponse queryPayment(@RequestBody EPaymentQuery query ) {
		
		return null;
	}
}
