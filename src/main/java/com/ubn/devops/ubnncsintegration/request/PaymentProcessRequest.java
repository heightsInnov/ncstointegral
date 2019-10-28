package com.ubn.devops.ubnncsintegration.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PaymentProcessRequest {

	private String customerEmail;

	private String externalRef;

	@JsonIgnore
	private String channel;

	private String account;

	private Date postingDate;

	private String amount;

	private String uniqueId;

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	/**
	 * @return the externalRef
	 */
	public String getExternalRef() {
		return externalRef;
	}

	/**
	 * @param externalRef the externalRef to set
	 */
	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;

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

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public String toString() {
		return "PaymentProcessRequest [customerEmail=" + customerEmail + ", externalRef=" + externalRef + ", channel="
				+ channel + ", account=" + account + ", postingDate=" + postingDate + ", amount=" + amount
				+ ", uniqueId=" + uniqueId + "]";
	}

}
