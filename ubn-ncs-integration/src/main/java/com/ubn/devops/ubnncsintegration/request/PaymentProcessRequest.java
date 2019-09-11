package com.ubn.devops.ubnncsintegration.request;

import java.util.Date;

public class PaymentProcessRequest {
	
	private String customerRef;
	
	private String transactionRef;
	
	private String channelCode;
	
	private String declarantCode;
	
	private Date postingDate = new Date();

	/**
	 * @return the customerRef
	 */
	public String getCustomerRef() {
		return customerRef;
	}

	/**
	 * @param customerRef the customerRef to set
	 */
	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}

	/**
	 * @return the transactionRef
	 */
	public String getTransactionRef() {
		return transactionRef;
	}

	/**
	 * @param transactionRef the transactionRef to set
	 */
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	/**
	 * @return the channelCode
	 */
	public String getChannelCode() {
		return channelCode;
	}

	/**
	 * @param channelCode the channelCode to set
	 */
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	/**
	 * @return the declarantCode
	 */
	public String getDeclarantCode() {
		return declarantCode;
	}

	/**
	 * @param declarantCode the declarantCode to set
	 */
	public void setDeclarantCode(String declarantCode) {
		this.declarantCode = declarantCode;
	}

	/**
	 * @return the postingDate
	 */
	public Date getPostingDate() {
		return postingDate;
	}

	/**
	 * @param postingDate the postingDate to set
	 */
	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	@Override
	public String toString() {
		return "PaymentProcessRequest [customerRef=" + customerRef + ", transactionRef=" + transactionRef
				+ ", channelCode=" + channelCode + ", declarantCode=" + declarantCode + ", postingDate=" + postingDate
				+ "]";
	}
	
	

}
