package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;
import java.util.UUID;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomMarshaller {

	static XmlMapper mapper = new XmlMapper();

	public static <T> T unmarshall(File xmlFile, Class<T> clazz){
		T obj = null;
		try {
			obj = mapper.readValue(xmlFile, clazz); // clazz.cast(unmarshaller.unmarshal(xmlFile));
		} catch (Exception ex) {
			log.error("Error occured while marshalling xmlfile to " + clazz.getName() + " because: ", ex.getMessage());
		}
		return obj;
	}

	public static int marshall(Object object, String folder) {
		int isMarshalled = 0;
		UUID uid = UUID.randomUUID();
		String filename = folder + uid.toString() + ".xml";
		try {
			mapper.writeValue(new File(filename), object);
			isMarshalled = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMarshalled;
	}

}
