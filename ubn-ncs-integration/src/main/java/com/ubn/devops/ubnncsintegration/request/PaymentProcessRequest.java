package com.ubn.devops.ubnncsintegration.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;

public class PaymentProcessRequest {

	private String customerEmail;

	private String externalRef;

	private String channel;

	private int sadYear;
	private String customsCode;
	private String sadAssessmentSerial;
	private String sadAssessmentNumber;
	private String version;

	private String account;

	@JsonFormat(lenient = OptBoolean.TRUE,pattern = "dd/MM/yyyy hh:mm:ss")
	private Date postingDate = new Date();

	private String amount;

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
	 * @return the sadYear
	 */
	public int getSadYear() {
		return sadYear;
	}

	/**
	 * @param sadYear the sadYear to set
	 */
	public void setSadYear(int sadYear) {
		this.sadYear = sadYear;
	}

	/**
	 * @return the customsCode
	 */
	public String getCustomsCode() {
		return customsCode;
	}

	/**
	 * @param customsCode the customsCode to set
	 */
	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}

	/**
	 * @return the sadAssessmentSerial
	 */
	public String getSadAssessmentSerial() {
		return sadAssessmentSerial;
	}

	/**
	 * @param sadAssessmentSerial the sadAssessmentSerial to set
	 */
	public void setSadAssessmentSerial(String sadAssessmentSerial) {
		this.sadAssessmentSerial = sadAssessmentSerial;
	}

	/**
	 * @return the sadAssessmentNumber
	 */
	public String getSadAssessmentNumber() {
		return sadAssessmentNumber;
	}

	/**
	 * @param sadAssessmentNumber the sadAssessmentNumber to set
	 */
	public void setSadAssessmentNumber(String sadAssessmentNumber) {
		this.sadAssessmentNumber = sadAssessmentNumber;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
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

	@Override
	public String toString() {
		return "PaymentProcessRequest [customerEmail=" + customerEmail + ", externalRef=" + externalRef + ", channel="
				+ channel + ", sadYear=" + sadYear + ", customsCode=" + customsCode + ", sadAssessmentSerial="
				+ sadAssessmentSerial + ", sadAssessmentNumber=" + sadAssessmentNumber + ", version=" + version
				+ ", account=" + account + ", postingDate=" + postingDate + ", amount=" + amount + "]";
	}

}
