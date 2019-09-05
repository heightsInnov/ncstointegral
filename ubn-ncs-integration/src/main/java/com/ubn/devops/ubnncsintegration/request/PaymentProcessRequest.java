package com.ubn.devops.ubnncsintegration.request;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
abstract
@Data
public class PaymentProcessRequest {
	
	private String customerRef;
	
	private String transactionRef;
	
	private String channelCode;
	
	private String declarantCode;
	
	private Date postingDate = new Date();
	
	

}
