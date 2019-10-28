package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
			@RequestParam String sadAssessmentSerial,@RequestParam String sadAssessmentNumber
			){
		return paymentDetailsService.fetchPaymentDetails(sadYear, customsCode, sadAssessmentSerial, sadAssessmentNumber);
	}
	
	@PostMapping("/process-payment")
	public ApiResponse processPayment(@RequestBody PaymentProcessRequest request,OAuth2Authentication details){
		request.setChannel(details.getOAuth2Request().getClientId());
		return paymentDetailsService.processPayment(request);
	}
	
	@GetMapping("/status")
	public ApiResponse getStatus(@RequestParam String uniqueId) {
		return paymentDetailsService.getStatus(uniqueId);
	}
	
}

