package com.ubn.devops.ubnncsintegration.utility;

public class FileReaderResponse {

	public static final String TRANSACTIONRESPONSE = "TransactionResponse";
	public static final String EASSESSMENTNOTICE = "EAssessmentNotice";
	public static final String EPAYMENTCONFIRMATION = "EPaymentConfirmation";
	public static final String EPAYMENTQUERY="EPaymentQuery";
	
	
	private String className;
	private Object object;

	public FileReaderResponse() {
		
	}
	
	
	public FileReaderResponse(String className, Object object) {
		super();
		this.className = className;
		this.object = object;
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	
}
