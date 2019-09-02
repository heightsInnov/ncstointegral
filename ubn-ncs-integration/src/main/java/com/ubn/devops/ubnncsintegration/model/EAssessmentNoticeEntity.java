package com.ubn.devops.ubnncsintegration.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.Tax;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(name="NCS_ASSESSMENT_NOTICE")
@Entity
@Data
public class EAssessmentNoticeEntity {
	
	@Transient
	private EAssessmentNotice assessmentNotice;
	
	@XmlTransient
	@JsonIgnore(false)
	@Id
	@GeneratedValue(generator = "noticegen",strategy = GenerationType.IDENTITY)
	@SequenceGenerator(allocationSize = 1,initialValue = 1,sequenceName = "noticegen",name="noticegen")
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
    @XmlTransient
    @JsonIgnore(false)
    private boolean isPaid = false;
    @OneToMany(fetch = FetchType.EAGER)
    private List<TaxEntity> taxes=new ArrayList<>();
    @Transient
    private double totalAmountToBePaid;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date createdDate = new Date();
    
    
    public EAssessmentNoticeEntity(EAssessmentNotice assessmentNotice) {
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
	
	public double getTotalAmountToBePaid() {
		return totalAmountToBePaid;
	}
	
	public void setTotalAmountToBePaid() {
		double amount = 0;
		for(TaxEntity t:taxes) {
			amount+=t.getTaxAmount();
		}
		this.totalAmountToBePaid = amount;
	}
    
    
}
