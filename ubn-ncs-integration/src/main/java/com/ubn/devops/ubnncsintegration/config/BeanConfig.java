
package com.ubn.devops.ubnncsintegration.config;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.concurrent.Executor;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ubn.devops.ubnncsintegration.utility.PropsReader;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class BeanConfig {
	
	 final String JNDI="JNDI";
	 final String USERNAMEPASSWORD="USERNAMEPASSWORD";

	@Autowired
	private FilePathsConfig filePathConfig;
	
	
	@Bean
	public DataSource dataSource() throws IllegalArgumentException, NamingException {
		String dbType = PropsReader.getValue("db.type");
		DataSource dataSource = null;
		if(dbType.contentEquals(USERNAMEPASSWORD)) {
			String driverclassName = PropsReader.getValue("db.driverclassname");
			String username = PropsReader.getValue("db.username");
			String password = PropsReader.getValue("db.password");
			String url = PropsReader.getValue("db.url");
			dataSource =  DataSourceBuilder.create()
					.driverClassName(driverclassName)
					.password(password).username(username)
					.url(url).build();
		}else if(dbType.equals(JNDI)) {
			String jndiName = PropsReader.getValue("db.jndiname");
			JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
			bean.setJndiName(jndiName);
			bean.setProxyInterface(DataSource.class);
	        bean.setLookupOnStartup(false);
	        bean.afterPropertiesSet();
	        dataSource = (DataSource) bean.getObject();
		}
		return dataSource;
	}
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean
	public WatchService watchService() {
		WatchService watchService = null;
		Kind<Path> kind = StandardWatchEventKinds.ENTRY_CREATE;
		try {
			watchService = FileSystems.getDefault().newWatchService();
			Path callbackPath = Paths.get(filePathConfig.getCallback());
			callbackPath.register(watchService, kind);
			Path eresponsePath = Paths.get(filePathConfig.getEresponse());
			eresponsePath.register(watchService, kind);
			Path errPath = Paths.get(filePathConfig.getErr());
			errPath.register(watchService, kind);			 
		} catch (Exception ex) {
			log.error("Unable to create watchservice bean because: " + ex.getMessage());
		}
		return watchService;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() throws IllegalArgumentException, NamingException {
		return new JdbcTemplate(dataSource());

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
