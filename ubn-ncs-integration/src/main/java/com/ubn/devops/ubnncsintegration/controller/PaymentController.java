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

import com.ubn.devops.ubnncsintegration.model.PaymentModel;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentQuery;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	

	@PostMapping("/payment-request")
	public ResponseEntity<PaymentModel> requestPayment(@RequestBody EPaymentConfirmation ePaymentConfirmation){
		PaymentModel entity = paymentService.updatePaymentDetailsWithRequest(ePaymentConfirmation);
		return new ResponseEntity<PaymentModel>(entity,HttpStatus.OK);
	}
		
	@GetMapping("/details")
	public ResponseEntity<ApiResponse> getPaymentDetails(@RequestParam String declarantCode){
		ApiResponse response= paymentService.fetchPaymentDetails(declarantCode);
		return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
	}
	
	@PostMapping("/send-query-request")
	public void sendQueryRequest(@RequestBody EPaymentQuery query ){
		//paymentService.queryTransaction(query);
	}

}
