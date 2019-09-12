package com.ubn.devops.ubnncsintegration.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpHeaders;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.bypass.HttpsServiceCertificateByPasser;
import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;

@Component
public class GenerateToken {
	
	private FilePathsConfig configs;
	
		
//	private String clientSecret;
//	private String clientId;
//	private String grantType;
//	private String baseUrlMiserv;
//	private String username;
//	private String password;
	
	HttpsURLConnection con = null;

	private HttpsServiceCertificateByPasser hscbp = new HttpsServiceCertificateByPasser();

	HttpHeaders headers;

	private static Logger logger = LoggerFactory.getLogger(GenerateToken.class.getName());

	@SuppressWarnings("unused")
	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(10000);
		factory.setConnectTimeout(10000);
		return factory;
	}

	public String getToken() throws IOException {
        String accesstoken = null;
        try {
            hscbp.disableCertificateValidation();
            
            String TOKEN_REST_URL = configs.getBaseUrlMiserv()+"username="+configs.getMgmusername()+"&password="+configs.getPass()+"&client_id="+configs.getClientId()+"&client_secret="+configs.getClientSecret()+"&grant_type="+configs.getGrantType();
           
            @SuppressWarnings("restriction")
			URL url = new URL(null, TOKEN_REST_URL, new sun.net.www.protocol.https.Handler());
            con = (HttpsURLConnection)url.openConnection();
            
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoInput(true);
            con.setDoOutput(true);

            JSONObject jsonEntity = new JSONObject();
            
            OutputStream os = con.getOutputStream();
            os.write(jsonEntity.toString().getBytes());
            os.flush();

            if (con.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + con.getResponseCode());
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
                String reader;
                String response = null;
                while ((reader = br.readLine()) != null) {
                    response = reader;
                }
                if (response != null) {
                    if (response.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("access_token")) {
                            accesstoken = jsonObject.getString("access_token");
                        }
                    }
                }
                con.disconnect();
            }
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("getToken::Response==== " + e.getMessage());
        }
        return accesstoken;
    }
}
