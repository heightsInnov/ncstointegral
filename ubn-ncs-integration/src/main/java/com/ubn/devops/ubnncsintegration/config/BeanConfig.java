package com.ubn.devops.ubnncsintegration.config;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BeanConfig {

	@Autowired
	private FilePathsConfig filePathConfig;
	
	@Autowired
	private DataSource dataSource;

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

}
