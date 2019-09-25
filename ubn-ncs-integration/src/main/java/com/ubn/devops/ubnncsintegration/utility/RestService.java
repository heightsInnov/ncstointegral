package com.ubn.devops.ubnncsintegration.utility;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubn.devops.restclient.constants.HttpMethods;
import com.ubn.devops.restclient.model.RestResponse;
import com.ubn.devops.restclient.serviceimpl.RestClientServiceImpl;
import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.response.FcubsValidationResponse;

@Component
public class RestService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	
	@Autowired
	private FilePathsConfig config;

	RestClientServiceImpl service = new RestClientServiceImpl();

	public String getAccessToken() {
		String accessToken = null;
		try {
			String url = config.getTokenurl() + "username=" + config.getUsername() + "&password=" + config.getPassword()
					+ "&client_id=" + config.getClientid() + "&client_secret=" + config.getClientsecret();
			RestResponse response = service.makeRequest(url, null, HttpMethods.POST, getHeaders());
			if (response != null && response.getCode() == 200) {
				JSONObject jsonObject = new JSONObject(response.getData());
				accessToken = jsonObject.getString("access_token");
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to get access token because: " + ex.getMessage(), ex);
		}
		return accessToken;
	}

	private Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		return headers;
	}

	public FcubsValidationResponse checkValidation(PaymentProcessRequest request) {
		log.info("Checking the validity of the transaction: "+request.toString());
		FcubsValidationResponse validationResponse = null;		
		try {
			String url= config.getRefvalidationurl()+getAccessToken();
			
			RestResponse response = service.makeRequest(url, request, HttpMethods.POST, getHeaders());
			log.info("Validation request responded with: "+response.getData());
			if(response!=null && response.getCode()==200) {
				String data = response.getData();
				JSONObject jsonResponse = new JSONObject(data);
				validationResponse = new FcubsValidationResponse();
				validationResponse.setStatus(jsonResponse.getInt("status"));
				validationResponse.setMessage(jsonResponse.getString("message"));
				
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to validate reference of FCUBS because: " + ex.getMessage());
		}
		return validationResponse;
	}

}