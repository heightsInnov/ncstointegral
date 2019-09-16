package com.ubn.devops.ubnncsintegration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@PropertySource({"${NCS_CONFIG}"})
@EnableScheduling
@SpringBootApplication
public class UbnNcsIntegrationApplication implements CommandLineRunner{
	
	/*
	 * @Autowired private Utils utilities;
	 */
	

	public static void main(String[] args) {
		SpringApplication.run(UbnNcsIntegrationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(System.getenv("NCS_CONFIG"));
		
	}


	/*
	 * @Scheduled(fixedDelay = 1500) public void watchFolder() {
	 * utilities.watchFolder(); }
	 */


}
