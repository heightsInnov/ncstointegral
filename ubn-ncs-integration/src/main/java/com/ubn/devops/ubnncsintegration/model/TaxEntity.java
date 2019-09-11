package com.ubn.devops.ubnncsintegration.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name = "NCS_TAX")
@Entity
public class TaxEntity {

	@Id
	@GeneratedValue(generator = "taxgen", strategy = GenerationType.IDENTITY)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "taxgen", name = "taxgen")
	private Long id;
	protected String taxCode;
	protected double taxAmount;
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
	public double getTaxAmount() {
		return taxAmount;
	}
	/**
	 * @param taxAmount the taxAmount to set
	 */
	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}
	
	
}
