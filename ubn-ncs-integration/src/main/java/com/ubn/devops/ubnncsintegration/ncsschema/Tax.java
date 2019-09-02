package com.ubn.devops.ubnncsintegration.ncsschema;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tax {
	@JsonProperty("TaxCode")
	private String taxCode;
	@JsonProperty("TaxAmount")
	private double taxAmount;

}
