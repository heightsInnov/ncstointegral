package com.ubn.devops.ubnncsintegration.ncsschema;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JacksonXmlRootElement(localName="Taxes")
public class Taxes {
	
	@JsonProperty(value = "Tax")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<Tax> tax = new ArrayList<>(); 
	
	public Taxes(List<Tax> taxesToBind) {
		this.tax =taxesToBind;
	}
	
	
}
