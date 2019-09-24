package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;

public class CustomMarshaller {

	private static Logger log = LoggerFactory.getLogger(CustomMarshaller.class);

	public static <T> T unmarshall(File xmlFile, Class<T> clazz) {
		XmlMapper mapper = new XmlMapper();
		T obj = null;
		try {
			obj = mapper.readValue(xmlFile, clazz); // clazz.cast(unmarshaller.unmarshal(xmlFile));
		} catch (Exception ex) {
			log.error("Error occured while marshalling xmlfile to " + clazz.getName() + " because: " + ex.getMessage(),
					ex);
		}
		return obj;
	}

	public static int marshall(Object object, List<String> folders, String type) {

		XmlMapper mapper = new XmlMapper();
		int isMarshalled = 0;
		String uid = UUID.randomUUID().toString().replace("-", "");
		try {			
			/*
			 * if (type.equals(FileReaderResponse.TRANSACTIONRESPONSE)) {
			 * 
			 * } else
			 */
		if (type.equals(FileReaderResponse.EPAYMENTCONFIRMATION)) {
			for(String folder:folders) {
				String filename = folder + FileReaderResponse.EPAYMENTCONFIRMATION + uid + ".xml";
				mapper.writeValue(new File(filename), object);
			}
			
		} /*
				 * else if (type.equals(FileReaderResponse.EPAYMENTQUERY)) { filename = folder +
				 * FileReaderResponse.EPAYMENTQUERY + uid + ".xml"; }
				 */
		    isMarshalled = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isMarshalled;
	}

	public static FileReaderResponse readFile(String filename) {
		FileReaderResponse response = null;
		try {
			XmlMapper mapper = new XmlMapper();
			String xml = Files.lines(Paths.get(filename)).collect(Collectors.joining("\n"));
			String xmlWithoutVersion = xml.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
			File file = new File(filename);
			if (file.exists()) {
				String[] xmlSplit = xmlWithoutVersion.split("\n");
				String header = xmlSplit[0];
				if (header.startsWith("<ePaymentConfirmation")) {
					EPaymentConfirmation confirm = mapper.readValue(file, EPaymentConfirmation.class);
					if (confirm != null) {
						response = new FileReaderResponse();
						response.setClassName(FileReaderResponse.EPAYMENTCONFIRMATION);
						response.setObject(confirm);
					}
				} else if (header.startsWith("<TransactionResponse")) {
					TransactionResponse transactionResponse = mapper.readValue(file, TransactionResponse.class);
					if (transactionResponse != null) {
						response = new FileReaderResponse();
						response.setClassName(FileReaderResponse.TRANSACTIONRESPONSE);
						response.setObject(transactionResponse);
					}

				} else if (header.startsWith("<eAssessmentNotice")) {
					EAssessmentNotice notice = mapper.readValue(file, EAssessmentNotice.class);
					if (notice != null) {
						response = new FileReaderResponse();
						response.setClassName(FileReaderResponse.EASSESSMENTNOTICE);
						response.setObject(notice);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	/*
	 * public static void main(String[] args) { try { String filePath =
	 * "C:\\Users\\lababatunde\\Documents\\TestData\\epay333695553658105.xml";
	 * FileReaderResponse response = CustomMarshaller.readFile(filePath); switch
	 * (response.getClassName()) { case FileReaderResponse.EASSESSMENTNOTICE:
	 * EAssessmentNotice notice = (EAssessmentNotice) response.getObject(); if
	 * (notice != null) { System.out .println("bankCode: " + notice.getBankCode() +
	 * " customsCode: " + notice.getCustomsCode());
	 * System.out.println(notice.toString()); } break; }
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } }
	 */

	public static void main(String[] args) {
		try {
			String filePath = "C:\\Users\\lababatunde\\Documents\\TestData\\epay3358698739543382.xml";
			FileReaderResponse response = CustomMarshaller.readFile(filePath);
			switch (response.getClassName()) {
			case FileReaderResponse.TRANSACTIONRESPONSE:
				TransactionResponse notice = (TransactionResponse) response.getObject();
				if (notice != null) {
					System.out.println(notice.getCustomsCode());
				}
				break;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}