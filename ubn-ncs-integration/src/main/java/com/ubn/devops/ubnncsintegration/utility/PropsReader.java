package com.ubn.devops.ubnncsintegration.utility;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropsReader {
	
	private static Logger log= LoggerFactory.getLogger(PropsReader.class);
	
	public static String getValue(String key) {
		String propsFile = System.getenv("NCS_CONFIG");
		String value = "";
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propsFile));
			value = props.getProperty(key);
		}catch(Exception ex) {
			log.error("error occured while trying to fetch value for: "+key+" because: "+ex.getMessage(),ex);
		}
		return value;
	}

}
