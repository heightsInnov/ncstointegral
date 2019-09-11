package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;

@RestController
public class TestController {
	

	
	@GetMapping("/get-value")
	public String getValue() {
		FilePathsConfig config = new FilePathsConfig();
		return config.getCliensecret();
	}

}
