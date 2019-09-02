package com.ubn.devops.ubnncsintegration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "ncs.path")
@Configuration
public class FilePathsConfig {

	private String assessmentnotice;
	private String paymentrequest;
	private String transactionresponse;
	private String paymentresponse;
	private String queryrequest;
	private String queryresponse;


}
