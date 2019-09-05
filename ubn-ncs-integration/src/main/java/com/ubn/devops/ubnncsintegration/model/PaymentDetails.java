package com.ubn.devops.ubnncsintegration.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.Tax;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentDetails {
	
	@Transient
	private EAssessmentNotice assessmentNotice;
	
	@XmlTransient
	@JsonIgnore(false)
	private Long id;
    private int sadYear;
    private String customsCode;
    private String declarantCode;
    private String declarantName;
    private String sadAssessmentSerial;
    private String sadAssessmentNumber;
    private String sadAssessmentDate;
    private String companyCode;
    private String companyName;
    private String bankCode;
    private String bankBranchCode;
    private String formMNumber;
    
    private String meansOfPayment;
	private String reference;
	private double amount;
	private String transactionstatus;
	private String responseMessage;
	
	private String paymentChannelCode;
	private String fcubsPostingRef;
	private String isSweepedToTsa;
	private String sweepFcubsPostRef;
	private Date sweepDate;
	private String hashValue;
	private String customerEmail;
	private String isPaymentReversed;
	private String reversalFcubsRef;
	private Date reversalDate;
	private Date postingDate;
	
	
    @XmlTransient
    @JsonIgnore(false)
    private boolean isPaid = false;
    private List<TaxEntity> taxes=new ArrayList<>();
    private double totalAmountToBePaid;
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a")
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss a")
    private Date createdDate = new Date();
    
    
    public PaymentDetails(EAssessmentNotice assessmentNotice) {
    	if(assessmentNotice!=null) {
    		double amount = 0;
    		this.sadYear=assessmentNotice.getSadYear();
    		this.customsCode=assessmentNotice.getCustomsCode();
    		this.declarantCode=assessmentNotice.getDeclarantCode();
    		this.declarantName=assessmentNotice.getDeclarantName();
    		this.sadAssessmentSerial=assessmentNotice.getSadAssessmentSerial();
    		this.sadAssessmentNumber=assessmentNotice.getSadAssessmentNumber();
    		this.sadAssessmentDate=assessmentNotice.getSadAssessmentDate();
    		this.companyCode=assessmentNotice.getCompanyCode();
    		this.companyName=assessmentNotice.getCompanyName();
    		this.bankBranchCode=assessmentNotice.getBankBranchCode();
    		this.bankCode=assessmentNotice.getBankCode();
    		this.formMNumber=assessmentNotice.getFormMNumber();
    		for(Tax t:assessmentNotice.getTaxes().getTax()){
    			TaxEntity taxEntity = new TaxEntity();
    			taxEntity.setTaxAmount(t.getTaxAmount());
    			taxEntity.setTaxCode(t.getTaxCode());
    			this.taxes.add(taxEntity);
    			amount+=t.getTaxAmount();
    		}
    		this.totalAmountToBePaid=amount;
    			
    	}
    }
    
}
