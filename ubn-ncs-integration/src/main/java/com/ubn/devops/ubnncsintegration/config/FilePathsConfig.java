package com.ubn.devops.ubnncsintegration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.utility.Encryptor;

@RefreshScope
@ConfigurationProperties(prefix = "ncs.path")
@Component
public class FilePathsConfig {

	private String assessmentnotice;
	private String paymentrequest;
	private String transactionresponse;
	private String paymentresponse;
	private String queryrequest;
	private String queryresponse;
	private String rootfolder;
	
	private String tokenurl;
	private String username;
	private String password;
	private String clientid;
	private String cliensecret;
	
	private String refvalidationurl;
	
	final Encryptor encryptor = new Encryptor(Encryptor.KEY);
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
	/**
	 * @return the tokenurl
	 */
	public String getTokenurl() {
		return tokenurl;
	}
	/**
	 * @param tokenurl the tokenurl to set
	 */
	public void setTokenurl(String tokenurl) {
		this.tokenurl = tokenurl;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return encryptor.decryptStringEncoded(username);
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return encryptor.decryptStringEncoded(password);
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the clientid
	 */
	public String getClientid() {
		return encryptor.decryptStringEncoded(clientid);
	}
	/**
	 * @param clientid the clientid to set
	 */
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	/**
	 * @return the cliensecret
	 */
	public String getCliensecret() {
		return encryptor.decryptStringEncoded(cliensecret);
	}
	/**
	 * @param cliensecret the cliensecret to set
	 */
	public void setCliensecret(String cliensecret) {
		this.cliensecret = cliensecret;
	}
	/**
	 * @return the refvalidationurl
	 */
	public String getRefvalidationurl() {
		return refvalidationurl;
	}
	/**
	 * @param refvalidationurl the refvalidationurl to set
	 */
	public void setRefvalidationurl(String refvalidationurl) {
		this.refvalidationurl = refvalidationurl;
	}

	

}
