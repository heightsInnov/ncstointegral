//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.08.27 at 12:06:57 PM WAT 
//


package com.ubn.devops.ubnncsintegration.ncsschema;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.ubn.devops.ubnncsintegration.model.EAssessmentNoticeEntity;
import com.ubn.devops.ubnncsintegration.model.TaxEntity;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@JacksonXmlRootElement(localName = "eAssessmentNotice")
public class EAssessmentNotice {

	@XmlTransient
	@JsonIgnore
	private EAssessmentNoticeEntity entity; 
	
	public EAssessmentNotice(EAssessmentNoticeEntity entity) {
		double totalAmount = 0;
		this.bankBranchCode = entity.getBankBranchCode();
		this.bankCode = entity.getBankCode();
		this.companyCode=entity.getCompanyCode();
		this.companyName=entity.getCompanyName();
		this.customsCode=entity.getCustomsCode();
		this.declarantCode=entity.getDeclarantCode();
		this.declarantName=entity.getDeclarantName();
		this.formMNumber=entity.getFormMNumber();
		this.SadAssessmentDate=entity.getSadAssessmentDate();
		this.SadAssessmentNumber=entity.getSadAssessmentNumber();
		this.SadAssessmentSerial=entity.getSadAssessmentSerial();
		this.SadYear=entity.getSadYear();
		List<Tax> taxesToBind = new ArrayList<>();
		for(TaxEntity taxEntity:entity.getTaxes()) {
			totalAmount +=taxEntity.getTaxAmount();
			Tax tax = new Tax();
			tax.setTaxAmount(taxEntity.getTaxAmount());
			tax.setTaxCode(taxEntity.getTaxCode());
			taxesToBind.add(tax);
		}
		this.taxes = new Taxes(taxesToBind);
		this.totalAmountToBePaid=totalAmount;
	}
	
    @JsonProperty("SADYear")
    private int SadYear;
    @JsonProperty("CustomsCode")
    protected String customsCode;
    @JsonProperty("DeclarantCode")
    protected String declarantCode;
    @JsonProperty("DeclarantName")
    protected String declarantName;
    @JsonProperty("SADAssessmentSerial")
    protected String SadAssessmentSerial;
    @JsonProperty("SADAssessmentNumber")
    protected String SadAssessmentNumber;
    @JsonProperty("SADAssessmentDate")
    protected String SadAssessmentDate;
    @JsonProperty("CompanyCode")
    protected String companyCode;
    @JsonProperty("CompanyName")
    protected String companyName;
    @JsonProperty("BankCode")
    protected String bankCode;
    @JsonProperty("BankBranchCode")
    protected String bankBranchCode;
    @JsonProperty("FormMNumber")
    protected String formMNumber;
    @JsonProperty("Taxes")
    protected Taxes taxes;
    @JsonProperty("TotalAmountToBePaid")
    protected double totalAmountToBePaid;
    
    

   }
