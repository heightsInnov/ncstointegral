package com.ubn.devops.ubnncsintegration.utility;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
//@NoArgsConstructor
@RequiredArgsConstructor
public class FileReaderResponse {

	public static final String TRANSACTIONRESPONSE = "TransactionResponse";
	public static final String EASSESSMENTNOTICE = "EAssessmentNotice";
	public static final String EPAYMENTCONFIRMATION = "EPaymentConfirmation";

	private String className;
	private Object object;

}
