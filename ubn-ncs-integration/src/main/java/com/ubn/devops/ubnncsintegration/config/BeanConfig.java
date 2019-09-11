package com.ubn.devops.ubnncsintegration.config;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class BeanConfig {

	
	private FilePathsConfig filePathConfig = new FilePathsConfig();
	
	@Autowired
	private DataSource dataSource;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean
	public WatchService watchService() {
		WatchService watchService = null;
		Kind<Path> kind = StandardWatchEventKinds.ENTRY_CREATE;
		try {
			watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(filePathConfig.getRootfolder());
			path.register(watchService, kind);
			/* 
			 * Path path = Paths.get(filePathConfig.getAssessmentnotice());
			 * path.register(watchService, kind); Path paymentResponsePath =
			 * Paths.get(filePathConfig.getPaymentresponse());
			 * paymentResponsePath.register(watchService, kind);
			 */
		} catch (Exception ex) {
			log.error("Unable to create watchservice bean because: " + ex.getMessage());
		}
		return watchService;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource);

	}
	
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Task-");
        return executor;
	}
	
		@Bean
	    public Docket api() { 
	        return new Docket(DocumentationType.SWAGGER_2)  
	          .select()                                  
	          .apis(RequestHandlerSelectors.any())              
	          .paths(PathSelectors.any())
	          .build()
	          .apiInfo(apiInfo());                                           
	    }
		
		private ApiInfo apiInfo() {
		    return new ApiInfo(
		      "NCS/Union Bank Integration Microservice", 
		      "NCS Integration with UBN", 
		      "API TOS", 
		      "Terms of service", 
		      new Contact("Devops", "www.unionbankng.com", "customerservice@unionbankng.com"), 
		      "License of API", "API license URL", Collections.emptyList());
		}

}
