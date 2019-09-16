
package com.ubn.devops.ubnncsintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ubn.devops.ubnncsintegration.utility.FolderWatch;

import springfox.documentation.swagger2.annotations.EnableSwagger2;



@EnableSwagger2
@EnableScheduling
@SpringBootApplication
public class UbnNcsIntegrationApplication{
	
	@Autowired
	private FolderWatch folderWatch;
	

	public static void main(String[] args) {
		SpringApplication.run(UbnNcsIntegrationApplication.class, args);
	}


	@Scheduled(fixedDelay = 1500)
	public void watchFolder() {
		folderWatch.watchFolder();
	}


}

