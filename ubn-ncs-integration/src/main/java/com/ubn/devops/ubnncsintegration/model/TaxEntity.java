package com.ubn.devops.ubnncsintegration.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Table(name = "NCS_TAX")
@Entity
@Data
public class TaxEntity {

	@Id
	@GeneratedValue(generator = "taxgen", strategy = GenerationType.IDENTITY)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, sequenceName = "taxgen", name = "taxgen")
	private Long id;
	protected String taxCode;
	protected double taxAmount;
}
