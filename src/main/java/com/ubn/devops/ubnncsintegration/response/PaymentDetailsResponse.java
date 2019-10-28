package com.ubn.devops.ubnncsintegration.response;

import java.math.BigDecimal;

public class PaymentDetailsResponse {

	private String companyName;
	private BigDecimal amount;
	private String declarantCode;
	private String declarantName;
	private int sadYear;
	private String sadAssessmentNumber;
	private String sadAssessmentSerial;
	private String customsCode;
	private String uniqueId;

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

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	

}
