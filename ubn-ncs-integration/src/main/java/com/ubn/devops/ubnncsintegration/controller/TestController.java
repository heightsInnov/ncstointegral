package com.ubn.devops.ubnncsintegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;

@RestController
public class TestController {
	
	@Autowired
	private FilePathsConfig config;

	
	@GetMapping("/get-value")
	public String getValue() {
		
		return config.getTokenurl();
	}

}
