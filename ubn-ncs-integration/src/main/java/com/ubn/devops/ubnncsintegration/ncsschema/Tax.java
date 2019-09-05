package com.ubn.devops.ubnncsintegration.ncsschema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Tax {
	@JsonProperty("TaxCode")
	private String taxCode;
	@JsonProperty("TaxAmount")
	private double taxAmount;

}
