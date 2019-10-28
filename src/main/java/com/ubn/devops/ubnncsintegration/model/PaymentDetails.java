package com.ubn.devops.ubnncsintegration.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.Tax;

public class PaymentDetails {

	private EAssessmentNotice assessmentNotice;

	public static final String PAYED = "1";

	public static final String PENDING = "0";
	
	public static final String IN_PROGRESS = "2";

	@JsonIgnore
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
	private String assessmentFilename;

	private BigDecimal amount;
	private String transactionstatus;
	private String responseMessage;
	private String responseCode;
	private String uniqueId;

	private String paymentChannelCode;
	private String fcubsPostingRef;
	private String customerEmail;
	private Date postingDate;

	private String isSweptOrReversed;
	private String sweeporReversedRef;
	private Date sweepOrReversedDate;

	@JsonIgnore
	private String paymentStatus = "0";
	private List<TaxEntity> taxes = new ArrayList<>();
	private BigDecimal totalAmountToBePaid;
	@JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss a")
	@DateTimeFormat(pattern = "dd-MM-yyyy hh:mm:ss a")
	private Date createdDate = new Date();
	private int  version;
	
	public PaymentDetails() {

	}

	public PaymentDetails(EAssessmentNotice assessmentNotice) {
		if (assessmentNotice != null) {
			double amount = 0;
			this.sadYear = assessmentNotice.getSadYear();
			this.customsCode = assessmentNotice.getCustomsCode();
			this.declarantCode = assessmentNotice.getDeclarantCode();

			this.declarantName = assessmentNotice.getDeclarantName().replace("\n", "");

			this.sadAssessmentSerial = assessmentNotice.getSadAssessmentSerial();
			this.sadAssessmentNumber = assessmentNotice.getSadAssessmentNumber();
			this.sadAssessmentDate = assessmentNotice.getSadAssessmentDate();
			this.companyCode = assessmentNotice.getCompanyCode();

			this.companyName = assessmentNotice.getCompanyName().replace("\n", "");

			this.bankBranchCode = assessmentNotice.getBankBranchCode();
			this.bankCode = assessmentNotice.getBankCode();
			this.formMNumber = assessmentNotice.getFormMNumber();
			this.version = assessmentNotice.getVersion();
			this.assessmentFilename=assessmentNotice.getAssessmentFilename();
			for (Tax t : assessmentNotice.getTaxes().getTax()) {
				TaxEntity taxEntity = new TaxEntity();
				taxEntity.setTaxAmount(t.getTaxAmount());
				taxEntity.setTaxCode(t.getTaxCode());
				this.taxes.add(taxEntity);
				amount += t.getTaxAmount().doubleValue();
			}
			this.totalAmountToBePaid = new BigDecimal(amount);

		}
	}

	/**
	 * @return the assessmentNotice
	 */
	public EAssessmentNotice getAssessmentNotice() {
		return assessmentNotice;
	}

	/**
	 * @param assessmentNotice the assessmentNotice to set
	 */
	public void setAssessmentNotice(EAssessmentNotice assessmentNotice) {
		this.assessmentNotice = assessmentNotice;
	}

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
	 * @return the sadYear
	 */
	public int getSadYear() {
		return sadYear;
	}

	/**
	 * @param sadYear the sadYear to set
	 */
	public void setSadYear(int sadYear) {
		this.sadYear = sadYear;
	}

	/**
	 * @return the customsCode
	 */
	public String getCustomsCode() {
		return customsCode;
	}

	/**
	 * @param customsCode the customsCode to set
	 */
	public void setCustomsCode(String customsCode) {
		this.customsCode = customsCode;
	}

	/**
	 * @return the declarantCode
	 */
	public String getDeclarantCode() {
		return declarantCode;
	}

	/**
	 * @param declarantCode the declarantCode to set
	 */
	public void setDeclarantCode(String declarantCode) {
		this.declarantCode = declarantCode;
	}

	/**
	 * @return the declarantName
	 */
	public String getDeclarantName() {
		return declarantName;
	}

	/**
	 * @param declarantName the declarantName to set
	 */
	public void setDeclarantName(String declarantName) {
		this.declarantName = declarantName;
	}

	/**
	 * @return the sadAssessmentSerial
	 */
	public String getSadAssessmentSerial() {
		return sadAssessmentSerial;
	}

	/**
	 * @param sadAssessmentSerial the sadAssessmentSerial to set
	 */
	public void setSadAssessmentSerial(String sadAssessmentSerial) {
		this.sadAssessmentSerial = sadAssessmentSerial;
	}

	/**
	 * @return the sadAssessmentNumber
	 */
	public String getSadAssessmentNumber() {
		return sadAssessmentNumber;
	}

	/**
	 * @param sadAssessmentNumber the sadAssessmentNumber to set
	 */
	public void setSadAssessmentNumber(String sadAssessmentNumber) {
		this.sadAssessmentNumber = sadAssessmentNumber;
	}

	/**
	 * @return the sadAssessmentDate
	 */
	public String getSadAssessmentDate() {
		return sadAssessmentDate;
	}

	/**
	 * @param sadAssessmentDate the sadAssessmentDate to set
	 */
	public void setSadAssessmentDate(String sadAssessmentDate) {
		this.sadAssessmentDate = sadAssessmentDate;
	}

	/**
	 * @return the companyCode
	 */
	public String getCompanyCode() {
		return companyCode;
	}

	/**
	 * @param companyCode the companyCode to set
	 */
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * @return the bankBranchCode
	 */
	public String getBankBranchCode() {
		return bankBranchCode;
	}

	/**
	 * @param bankBranchCode the bankBranchCode to set
	 */
	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	/**
	 * @return the formMNumber
	 */
	public String getFormMNumber() {
		return formMNumber;
	}

	/**
	 * @param formMNumber the formMNumber to set
	 */
	public void setFormMNumber(String formMNumber) {
		this.formMNumber = formMNumber;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {

		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the transactionstatus
	 */
	public String getTransactionstatus() {
		return transactionstatus;
	}

	/**
	 * @param transactionstatus the transactionstatus to set
	 */
	public void setTransactionstatus(String transactionstatus) {
		this.transactionstatus = transactionstatus;
	}

	/**
	 * @return the responseMessage
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	/**
	 * @return the paymentChannelCode
	 */
	public String getPaymentChannelCode() {
		return paymentChannelCode;
	}

	/**
	 * @param paymentChannelCode the paymentChannelCode to set
	 */
	public void setPaymentChannelCode(String paymentChannelCode) {
		this.paymentChannelCode = paymentChannelCode;
	}

	/**
	 * @return the fcubsPostingRef
	 */
	public String getFcubsPostingRef() {
		return fcubsPostingRef;
	}

	/**
	 * @param fcubsPostingRef the fcubsPostingRef to set
	 */
	public void setFcubsPostingRef(String fcubsPostingRef) {
		this.fcubsPostingRef = fcubsPostingRef;
	}

	
	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	/**
	 * @return the postingDate
	 */
	public Date getPostingDate() {
		return postingDate;
	}

	/**
	 * @param postingDate the postingDate to set
	 */
	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	/**
	 * @return the taxes
	 */
	public List<TaxEntity> getTaxes() {
		return taxes;
	}

	/**
	 * @param taxes the taxes to set
	 */
	public void setTaxes(List<TaxEntity> taxes) {
		this.taxes = taxes;
	}

	/**
	 * @return the totalAmountToBePaid
	 */
	public BigDecimal getTotalAmountToBePaid() {

		return totalAmountToBePaid;
	}

	/**
	 * @param totalAmountToBePaid the totalAmountToBePaid to set
	 */
	public void setTotalAmountToBePaid(BigDecimal totalAmountToBePaid) {

		this.totalAmountToBePaid = totalAmountToBePaid;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the assessmentFilename
	 */
	public String getAssessmentFilename() {
		return assessmentFilename;
	}

	/**
	 * @param assessmentFilename the assessmentFilename to set
	 */
	public void setAssessmentFilename(String assessmentFilename) {
		this.assessmentFilename = assessmentFilename;
	}

	/**
	 * @return the paymentStatus
	 */
	public String getPaymentStatus() {
		return paymentStatus;
	}

	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/**
	 * @return the isSweptOrReversed
	 */
	public String getIsSweptOrReversed() {
		return isSweptOrReversed;
	}

	/**
	 * @param isSweptOrReversed the isSweptOrReversed to set
	 */
	public void setIsSweptOrReversed(String isSweptOrReversed) {
		this.isSweptOrReversed = isSweptOrReversed;
	}

	/**
	 * @return the sweeporReversedRef
	 */
	public String getSweeporReversedRef() {
		return sweeporReversedRef;
	}

	/**
	 * @param sweeporReversedRef the sweeporReversedRef to set
	 */
	public void setSweeporReversedRef(String sweeporReversedRef) {
		this.sweeporReversedRef = sweeporReversedRef;
	}

	/**
	 * @return the sweepOrReversedDate
	 */
	public Date getSweepOrReversedDate() {
		return sweepOrReversedDate;
	}

	/**
	 * @param sweepOrReversedDate the sweepOrReversedDate to set
	 */
	public void setSweepOrReversedDate(Date sweepOrReversedDate) {
		this.sweepOrReversedDate = sweepOrReversedDate;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

}
