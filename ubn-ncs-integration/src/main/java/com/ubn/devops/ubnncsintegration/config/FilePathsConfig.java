package com.ubn.devops.ubnncsintegration.config;

import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.utility.Encryptor;
import com.ubn.devops.ubnncsintegration.utility.PropsReader;

@Component
public class FilePathsConfig {
	
	private String assessmentnotice="ncs.path.assessmentnotice";
	private String paymentrequest="ncs.path.paymentrequest";
	private String transactionresponse="ncs.path.transactionresponse";
	private String paymentresponse="ncs.path.paymentresponse";
	private String queryrequest="ncs.path.queryrequest";
	private String queryresponse="ncs.path.queryresponse";
	private String rootfolder="ncs.path.rootfolder";
	
	//where webfontaine will drop their files
	private String err="ncs.path.err";
	private String callback="ncs.path.callback";
	private String eresponse="ncs.path.eresponse";
	private String infolder="ncs.path.in";
	
	private String tokenurl="ncs.path.tokenurl";
	private String username="ncs.path.username";
	private String password="ncs.path.password";
	private String clientid="ncs.path.clientid";
	private String cliensecret="ncs.path.cliensecret";
	
	private String refvalidationurl="ncs.path.refvalidationurl";
	
	
	private String ubntransitgl="ncs.sweep.gl.account";

	private String ubntransitgl_name="ncs.sweep.gl.account.name";

	private String tsaaccountno="ncs.sweep.tsa.account";
	
	private String tsaaccount_name="ncs.sweep.tsa.account.name";

	private String tsaaccount_branch="ncs.sweep.tsa.account.branchcode";

	private String narations="ncs.sweep.trans.notification";

	private String init_branch="ncs.sweep.gl.branch";

	private String token_url="ncs.token.url";

	private String posting_url="ncs.posting.url";

	private String accteqry_url="ncs.accteqry.url";
	
	private String clientSecret = "mgm.ubn.miserv.clientsecret";
	
	private String clientId = "mgm.ubn.miserv.clientid";
	private String grantType = "mgm.ubn.miserv.granttype";
	private String mgmusername = "mgm.ubn.miserv.username";
	private String pass = "mgm.ubn.miserv.password";
	private String baseUrlMiserv = "miservurl";
	
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

	/**
	 * @return the ubntransitgl
	 */
	public String getUbntransitgl() {
		return PropsReader.getValue(ubntransitgl);
	}

	/**
	 * @return the ubntransitgl_name
	 */
	public String getUbntransitgl_name() {
		return PropsReader.getValue(ubntransitgl_name);
	}

	/**
	 * @return the tsaaccountno
	 */
	public String getTsaaccountno() {
		return PropsReader.getValue(tsaaccountno);
	}

	/**
	 * @return the tsaaccount_name
	 */
	public String getTsaaccount_name() {
		return PropsReader.getValue(tsaaccount_name);
	}

	/**
	 * @return the tsaaccount_branch
	 */
	public String getTsaaccount_branch() {
		return PropsReader.getValue(tsaaccount_branch);
	}

	/**
	 * @return the narations
	 */
	public String getNarations() {
		return PropsReader.getValue(narations);
	}

	/**
	 * @return the init_branch
	 */
	public String getInit_branch() {
		return PropsReader.getValue(init_branch);
	}

	/**
	 * @return the token_url
	 */
	public String getToken_url() {
		return PropsReader.getValue(token_url);
	}

	/**
	 * @return the posting_url
	 */
	public String getPosting_url() {
		return PropsReader.getValue(posting_url);
	}

	/**
	 * @return the accteqry_url
	 */
	public String getAccteqry_url() {
		return PropsReader.getValue(accteqry_url);
	}

	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @return the grantType
	 */
	public String getGrantType() {
		return grantType;
	}

	/**
	 * @return the mgmusername
	 */
	public String getMgmusername() {
		return mgmusername;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @return the baseUrlMiserv
	 */
	public String getBaseUrlMiserv() {
		return baseUrlMiserv;
	}

	/**
	 * @return the err
	 */
	public String getErr() {
		return PropsReader.getValue(err);
	}

	/**
	 * @return the callback
	 */
	public String getCallback() {
		return PropsReader.getValue(callback);
	}

	/**
	 * @return the eresponse
	 */
	public String getEresponse() {
		return PropsReader.getValue(eresponse);
	}

	/**
	 * @return the infolder
	 */
	public String getInfolder() {
		return PropsReader.getValue(infolder);
	}
	
	
	

}
