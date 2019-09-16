package com.ubn.devops.ubnncsintegration.sweep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ubn.devops.ubnncsintegration.bypass.HttpsServiceCertificateByPasser;
import com.ubn.devops.ubnncsintegration.model.AccountEnquiry;
import com.ubn.devops.ubnncsintegration.response.SweepReverseResponse;

@Component
public class SweepingAndPosting {
	HttpURLConnection con = null;
	private HttpsServiceCertificateByPasser hscbp = new HttpsServiceCertificateByPasser();
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(120000);
		factory.setConnectTimeout(120000);
		return factory;
	}

	public SweepReverseResponse PostorSweep1(String miserv_url, JSONObject request) {
		SweepReverseResponse response = new SweepReverseResponse();
		// Default response
		String CONNECTION_URL = miserv_url;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
			// disableSslVerification();
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
						if (jsonObject.getString("reference") != null && jsonObject.getString("cbaBatchNo") != null) {
							response.setBody(
									jsonObject.getString("reference") + "|" + jsonObject.getString("cbaBatchNo"));
						} else if (jsonObject.getString("reference") == null
								&& jsonObject.getString("cbaBatchNo") != null) {
							response.setBody(jsonObject.getString("cbaBatchNo"));
						} else if (jsonObject.getString("reference") != null
								&& jsonObject.getString("cbaBatchNo") == null) {
							response.setBody(jsonObject.getString("reference"));
						} else {
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

	public SweepReverseResponse PostorSweep(String methodUrl, JSONObject request) throws IOException {
		SweepReverseResponse response = new SweepReverseResponse();
		logger.info("Request to Miserv >>" + request);
		String CONNECTION_URL = methodUrl;
		try {
			hscbp.disableCertificateValidation();

			@SuppressWarnings("restriction")
			URL url = new URL(null, CONNECTION_URL, new sun.net.www.protocol.https.Handler());
			con = (HttpsURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoInput(true);
			con.setDoOutput(true);

			OutputStream os = con.getOutputStream();
			os.write(request.toString().getBytes());
//			os.flush();

			if (con.getResponseCode() != 200) {
				logger.info("Error response from Miserv >>" + con.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
				String reader;
				String result = null;
				while ((reader = br.readLine()) != null) {
					result = reader;
					logger.info("Result from Miserve >>" + result);
				}
				if (result != null) {
					if (result.startsWith("{")) {
						JSONObject jsonObject = new JSONObject(result);
						if (CONNECTION_URL.contains("cbaaccountenquiry")) {
							if (jsonObject.getString("code").equals("00")) {
								AccountEnquiry accountEnquiry = new AccountEnquiry(
										jsonObject.getString("accountNumber"), jsonObject.getString("accountName"),
										jsonObject.getString("accountBranchCode"),
										jsonObject.getString("customerNumber"), jsonObject.getString("accountCurrency"),
										jsonObject.getString("accountType"), jsonObject.getDouble("availableBalance"),
										jsonObject.optString("customerEmail"),
										jsonObject.optString("customerPhoneNumber"));
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
							if (!jsonObject.optString("reference").isEmpty()
									&& !jsonObject.optString("cbaBatchNo").isEmpty()) {
								response.setBody(
										jsonObject.optString("reference") + "|" + jsonObject.optString("cbaBatchNo"));
							} else if (jsonObject.optString("reference").isEmpty()
									&& !jsonObject.optString("cbaBatchNo").isEmpty()) {
								response.setBody(jsonObject.getString("cbaBatchNo"));
							} else if (!jsonObject.optString("reference").isEmpty()
									&& jsonObject.optString("cbaBatchNo").isEmpty()) {
								response.setBody(jsonObject.optString("reference"));
							} else {
								response.setBody("empty");
							}
						}
					}
				}
				con.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getToken::Response==== " + e.getMessage());
		}
		return response;
	}
}
