package com.ubn.devops.ubnncsintegration.utility;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropsReader {
	
	private static Logger log= LoggerFactory.getLogger(PropsReader.class);
	private static final String FILEPATH = "C:/UONLINE_HOME/ncsproperties.properties"; 
	
	public static String getValue(String key) {
		String value = "";
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(FILEPATH));
			value = props.getProperty(key);
		}catch(Exception ex) {
			log.error("error occured while trying to fetch value for: "+key+" because: "+ex.getMessage(),ex);
		}
		return value;
	}

}
