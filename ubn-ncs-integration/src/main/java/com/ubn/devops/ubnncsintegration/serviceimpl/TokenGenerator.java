package com.ubn.devops.ubnncsintegration.serviceimpl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;

@Component
public class TokenGenerator {


	@Autowired
	private FilePathsConfig configs;
	

	private static Logger logger = LoggerFactory.getLogger(TokenGenerator.class.getName());

	
//	public static void disableSslVerification() {
//        try
//        {
//            // Create a trust manager that does not validate certificate chains
//            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//              
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }
//            };
//
//            // Install the all-trusting trust manager
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            // Create all-trusting host name verifier
//            HostnameVerifier allHostsValid = new HostnameVerifier() {
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            };
//
//            // Install the all-trusting host verifier
//            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//	}

        
	static {
		disableSslVerification();
	}

	private static void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
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

	// -----Setting time out for service operation
	private static ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(100000);
		factory.setConnectTimeout(100000);
		return factory;
	}

	public String getToken() {
		logger.info("MiservURL = **" +configs.getBaseUrlMiserv()  + "**");
		String access_token="";
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			JSONObject data = new JSONObject();
			data.put("client_secret", configs.getCliensecret());
			data.put("client_id", configs.getClientId());
			data.put("grant_type", configs.getGrantType());
			data.put("username", configs.getUsername());
			data.put("password", configs.getPass());
			
			// HttpEntity<String>: To get result as String.
			HttpEntity<String> entity = new HttpEntity<>(data.toString(), headers);
			disableSslVerification();
			RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

			
			String TOKEN_REST_URL = configs.getBaseUrlMiserv();
			
//			+ "client_secret=" + URLEncoder.encode(clientSecret) + "&client_id=" + URLEncoder.encode(clientId)
//					+ "&grant_type=" + grantType + "&username=" + URLEncoder.encode(user) + "&password=" + URLEncoder.encode(pass);

			logger.info("the url is? "+TOKEN_REST_URL);
			// Send request with POST method, and Headers.
			ResponseEntity<String> response = restTemplate.exchange(TOKEN_REST_URL, HttpMethod.POST, entity, String.class);
			String result = response.getBody();

			if (result != null) {
				if (result.startsWith("{")) {
					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.has("access_token")) {
						access_token =  jsonObject.optString("access_token");
					}
				}
			} else {
				logger.error(new Date() + "::Method::getToken::Response::" + response);
			}
		} catch (Exception e) {
			logger.error(new Date() + "::Method::getToken::Response::" + e.getMessage());
		}
		return access_token;
	}
}
