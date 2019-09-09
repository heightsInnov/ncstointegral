package com.ubn.devops.ubnncsintegration.response;

import java.math.BigDecimal;

public class PaymentDetailsResponse {

	private String companyName;
	private BigDecimal amount;
	private String formMNumber;
	private String declarantCode;
	private String declarantName;

	public PaymentDetailsResponse() {

	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the formMNumber
	 */
	public String getFormMNumber() {
		return formMNumber;
	}

	/**
	 * @param formMNumber the formMNumber to set
	 */
	public void setFormMNumber(String formMNumber) {
		this.formMNumber = formMNumber;
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
	 * @return the declarantName
	 */
	public String getDeclarantName() {
		return declarantName;
	}

	/**
	 * @param declarantName the declarantName to set
	 */
	public void setDeclarantName(String declarantName) {
		this.declarantName = declarantName;
	}

}
