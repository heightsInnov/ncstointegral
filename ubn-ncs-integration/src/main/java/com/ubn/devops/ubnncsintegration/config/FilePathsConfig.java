package com.ubn.devops.ubnncsintegration.config;

import com.ubn.devops.ubnncsintegration.utility.Encryptor;
import com.ubn.devops.ubnncsintegration.utility.PropsReader;

public class FilePathsConfig {
	
	private String assessmentnotice="ncs.path.assessmentnotice";
	private String paymentrequest="ncs.path.paymentrequest";
	private String transactionresponse="ncs.path.transactionresponse";
	private String paymentresponse="ncs.path.paymentresponse";
	private String queryrequest="ncs.path.queryrequest";
	private String queryresponse="ncs.path.queryresponse";
	private String rootfolder="ncs.path.rootfolder";
	
	private String tokenurl="ncs.path.tokenurl";
	private String username="ncs.path.username";
	private String password="ncs.path.password";
	private String clientid="ncs.path.clientid";
	private String cliensecret="ncs.path.cliensecret";
	
	private String refvalidationurl="ncs.path.refvalidationurl";
	
	final Encryptor encryptor = new Encryptor(Encryptor.KEY);
	/**
	 * @return the assessmentnotice
	 */
	public String getAssessmentnotice() {
		return assessmentnotice;
	}
	
	/**
	 * @return the paymentrequest
	 */
	public String getPaymentrequest() {
		return PropsReader.getValue(paymentrequest);
	}
	
	/**
	 * @return the transactionresponse
	 */
	public String getTransactionresponse() {
		return PropsReader.getValue(transactionresponse);
	}
	
	/**
	 * @return the paymentresponse
	 */
	public String getPaymentresponse() {
		return PropsReader.getValue(paymentresponse);
	}
	
	/**
	 * @return the queryrequest
	 */
	public String getQueryrequest() {
		return PropsReader.getValue(queryrequest);
	}

	/**
	 * @return the queryresponse
	 */
	public String getQueryresponse() {
		return PropsReader.getValue(queryresponse);
	}
	
	/**
	 * @return the rootfolder
	 */
	public String getRootfolder() {
		return PropsReader.getValue(rootfolder);
	}
	
	/**
	 * @return the tokenurl
	 */
	public String getTokenurl() {
		return PropsReader.getValue(tokenurl);
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return encryptor.decryptStringEncoded(PropsReader.getValue(username));
	}
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return encryptor.decryptStringEncoded(PropsReader.getValue(password));
	}
	
	/**
	 * @return the clientid
	 */
	public String getClientid() {
		return encryptor.decryptStringEncoded(PropsReader.getValue(clientid));
	}

	/**
	 * @return the cliensecret
	 */
	public String getCliensecret() {
		return encryptor.decryptStringEncoded(PropsReader.getValue(cliensecret));
	}

	public String getRefvalidationurl() {
		return PropsReader.getValue(refvalidationurl);
	}
	

	

}
