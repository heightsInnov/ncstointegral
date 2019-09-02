package com.ubn.devops.ubnncsintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ubn.devops.ubnncsintegration.utility.Utils;

@EnableScheduling
@SpringBootApplication
public class UbnNcsIntegrationApplication implements CommandLineRunner{

	@Autowired
	private Utils utilities;

	public static void main(String[] args) {
		SpringApplication.run(UbnNcsIntegrationApplication.class, args);
	}


	@Scheduled(fixedDelay = 1500)
	public void watchFolder() {
		utilities.watchFolder();
	}


	@Override
	public void run(String... args) throws Exception {
		
	}

}
