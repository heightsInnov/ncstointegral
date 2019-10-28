package com.ubn.devops.ubnncsintegration.ncsschema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName="Taxes")
public class Taxes {
	
	@JsonProperty(value = "Tax")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<Tax> tax = new ArrayList<>(); 
	
	public Taxes() {
		
	}
	
	public Taxes(List<Tax> taxesToBind) {
		this.tax =taxesToBind;
	}

	/**
	 * @return the tax
	 */
	public List<Tax> getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(List<Tax> tax) {
		this.tax = tax;
	}
	
	
	
	
}
