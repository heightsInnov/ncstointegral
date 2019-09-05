//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.27 at 12:04:59 PM WAT 
//


package com.ubn.devops.ubnncsintegration.ncsschema;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Data
@JacksonXmlRootElement(localName = "ePaymentConfirmation")
public class EPaymentConfirmation {

	@JsonProperty("CustomsCode")
    protected String customsCode;
	@JsonProperty( "DeclarantCode")
    protected String declarantCode;
	@JsonProperty( "BankCode")
    protected String bankCode;
	@JsonProperty( "SadAsmt")
    protected SadAsmt sadAsmt;
	@JsonProperty( "Payment")
	@JacksonXmlElementWrapper(useWrapping = false)
    protected List<Payment> payment;
	@JsonProperty( "TotalAmountToBePaid")
    protected double totalAmountToBePaid;
    
}
