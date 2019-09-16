package com.ubn.devops.ubnncsintegration.serviceimpl;

import org.springframework.stereotype.Component;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.ubn.devops.ubnncsintegration.model.AccountEnquiry;
import com.ubn.devops.ubnncsintegration.response.SweepReverseResponse;

@Component
public class SweepingAndPosting {
	
	public void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(120000);
		factory.setConnectTimeout(120000);
		return factory;
	}

	public SweepReverseResponse PostorSweep(String miserv_url, JSONObject request) {
		SweepReverseResponse response = new SweepReverseResponse();
		// Default response
		String CONNECTION_URL = miserv_url;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
			disableSslVerification();
			RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

			// Send request with POST method, and Headers.
			ResponseEntity<String> responseFromServer = restTemplate.exchange(CONNECTION_URL, HttpMethod.POST, entity,
					String.class);

			String result = responseFromServer.getBody();

			if (result != null) {
				if (result.startsWith("{")) {
					JSONObject jsonObject = new JSONObject(result);
					if (CONNECTION_URL.contains("cbaaccountenquiry")) {
						if (jsonObject.getString("code").equals("00")) {
							AccountEnquiry accountEnquiry = new AccountEnquiry(jsonObject.getString("accountNumber"),
									jsonObject.getString("accountName"), jsonObject.getString("accountBranchCode"),
									jsonObject.getString("customerNumber"), jsonObject.getString("accountCurrency"),
									jsonObject.getString("accountType"), jsonObject.getDouble("availableBalance"),
									jsonObject.getString("customerEmail"), jsonObject.getString("customerPhoneNumber"));
							response.setCode(jsonObject.getString("code"));
							response.setMessage(jsonObject.getString("message"));
							response.setData(accountEnquiry);
						} else if (jsonObject.getString("code").equals("25")) {
							response.setCode(jsonObject.getString("code"));
							response.setMessage(jsonObject.getString("message"));
						}
					} else {
						response.setCode(jsonObject.getString("code"));
						response.setMessage(jsonObject.getString("message"));
						if(jsonObject.getString("reference")!= null && jsonObject.getString("cbaBatchNo")!= null) {
							response.setBody(jsonObject.getString("reference") +"|"+ jsonObject.getString("cbaBatchNo"));
						}else if(jsonObject.getString("reference")== null && jsonObject.getString("cbaBatchNo")!= null) {
							response.setBody(jsonObject.getString("cbaBatchNo"));
						}else if(jsonObject.getString("reference")!= null && jsonObject.getString("cbaBatchNo")== null) {
							response.setBody(jsonObject.getString("reference"));
						}else {
							response.setBody(null);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = new SweepReverseResponse("100", e.getMessage());
		}
		return response;
	}
}
