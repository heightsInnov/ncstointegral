package com.ubn.devops.ubnncsintegration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@ConfigurationProperties(prefix = "ncs.path")
@Configuration
public class FilePathsConfig {

	private String assessmentnotice;
	private String paymentrequest;
	private String transactionresponse;
	private String paymentresponse;
	private String queryrequest;
	private String queryresponse;
	private String rootfolder;
	/**
	 * @return the assessmentnotice
	 */
	public String getAssessmentnotice() {
		return assessmentnotice;
	}
	/**
	 * @param assessmentnotice the assessmentnotice to set
	 */
	public void setAssessmentnotice(String assessmentnotice) {
		this.assessmentnotice = assessmentnotice;
	}
	/**
	 * @return the paymentrequest
	 */
	public String getPaymentrequest() {
		return paymentrequest;
	}
	/**
	 * @param paymentrequest the paymentrequest to set
	 */
	public void setPaymentrequest(String paymentrequest) {
		this.paymentrequest = paymentrequest;
	}
	/**
	 * @return the transactionresponse
	 */
	public String getTransactionresponse() {
		return transactionresponse;
	}
	/**
	 * @param transactionresponse the transactionresponse to set
	 */
	public void setTransactionresponse(String transactionresponse) {
		this.transactionresponse = transactionresponse;
	}
	/**
	 * @return the paymentresponse
	 */
	public String getPaymentresponse() {
		return paymentresponse;
	}
	/**
	 * @param paymentresponse the paymentresponse to set
	 */
	public void setPaymentresponse(String paymentresponse) {
		this.paymentresponse = paymentresponse;
	}
	/**
	 * @return the queryrequest
	 */
	public String getQueryrequest() {
		return queryrequest;
	}
	/**
	 * @param queryrequest the queryrequest to set
	 */
	public void setQueryrequest(String queryrequest) {
		this.queryrequest = queryrequest;
	}
	/**
	 * @return the queryresponse
	 */
	public String getQueryresponse() {
		return queryresponse;
	}
	/**
	 * @param queryresponse the queryresponse to set
	 */
	public void setQueryresponse(String queryresponse) {
		this.queryresponse = queryresponse;
	}
	/**
	 * @return the rootfolder
	 */
	public String getRootfolder() {
		return rootfolder;
	}
	/**
	 * @param rootfolder the rootfolder to set
	 */
	public void setRootfolder(String rootfolder) {
		this.rootfolder = rootfolder;
	}

	

}
