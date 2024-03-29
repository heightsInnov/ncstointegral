package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	@GetMapping("/details")
	public ApiResponse getPaymentDetails(@RequestParam int sadYear,@RequestParam String customsCode,
			@RequestParam String sadAssessmentSerial,@RequestParam String sadAssessmentNumber,
			@RequestParam String version){
		return paymentDetailsService.fetchPaymentDetails(sadYear, customsCode, sadAssessmentSerial, sadAssessmentNumber, version);
	}
	
	@PostMapping("/process-payment")
	public ApiResponse processPayment(@RequestBody PaymentProcessRequest request){
		return paymentDetailsService.processPayment(request);
	}
	
}

