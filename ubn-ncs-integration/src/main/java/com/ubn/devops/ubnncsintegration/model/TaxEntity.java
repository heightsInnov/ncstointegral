package com.ubn.devops.ubnncsintegration.model;

import java.math.BigDecimal;

public class TaxEntity {

	private Long id;
	private String taxCode;
	private BigDecimal taxAmount;
	private Long paymentDetailsId;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}

	/**
	 * @param taxCode the taxCode to set
	 */
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * @return the taxAmount
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount the taxAmount to set
	 */
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * @return the paymentDetailsId
	 */
	public Long getPaymentDetailsId() {
		return paymentDetailsId;
	}

	/**
	 * @param paymentDetailsId the paymentDetailsId to set
	 */
	public void setPaymentDetailsId(Long paymentDetailsId) {
		this.paymentDetailsId = paymentDetailsId;
	}

}
