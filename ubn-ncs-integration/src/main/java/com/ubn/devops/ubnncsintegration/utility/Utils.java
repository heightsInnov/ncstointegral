package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.EAssessmentNoticeEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.Info;
import com.ubn.devops.ubnncsintegration.ncsschema.SadAsmt;
import com.ubn.devops.ubnncsintegration.ncsschema.TRS;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.service.EAssessmentNoticeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Utils {

	@Autowired
	private FilePathsConfig filePathConfig;

	@Autowired
	private EAssessmentNoticeService noticeService;

	@Autowired
	private WatchService watchService;

	public void watchFolder() {
		EAssessmentNotice eAssessmentNotice = null;
		XmlMapper mapper = new XmlMapper();
		String assessmentPath = null;
		String paymentRequestPath = null;
		File assessmentXmlFile = null;
		File paymentRequestFile = null;
		try {
			WatchKey watchKey;
			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					assessmentPath = filePathConfig.getAssessmentnotice() + event.context();
					if (isFileExists(assessmentPath)) {
						assessmentXmlFile = new File(assessmentPath);
						try {
							eAssessmentNotice = CustomMarshaller.unmarshall(assessmentXmlFile, EAssessmentNotice.class);
						} catch (Exception ex) {
							log.error("Error occured while trying to unmarshall assessmentxml file because: "
									+ ex.getMessage());
						}
						if (eAssessmentNotice != null) {
							// Persist eAssessmentNotice to database
							noticeService.saveAssessmentNotice(eAssessmentNotice);
							// Then go ahead and confirm that the notice receipt
							TransactionResponse response = new TransactionResponse();
							response.setCustomsCode(eAssessmentNotice.getCustomsCode());
							response.setDeclarantCode(eAssessmentNotice.getDeclarantCode());
							response.setTransactionStatus(TRS.OK);
							Info info = new Info();
							info.setMessage(Arrays.asList("Successfully received the assessment notice"));
							response.setInfo(info);
							SadAsmt sadAsmt = new SadAsmt();
							sadAsmt.setSadAssessmentNumber(eAssessmentNotice.getSadAssessmentNumber());
							sadAsmt.setSadAssessmentSerial(eAssessmentNotice.getSadAssessmentSerial());
							sadAsmt.setSadYear(eAssessmentNotice.getSadYear());
							response.setSadAsmt(sadAsmt);
							//create xml of transaction respons in the transaction response folder
							CustomMarshaller.marshall(response, filePathConfig.getTransactionresponse());
							
						}
						// file.delete();
					}
					paymentRequestPath = filePathConfig.getPaymentrequest() + event.context();
					if (isFileExists(paymentRequestPath)) {
						paymentRequestFile = new File(paymentRequestPath);
						try {

						} catch (Exception ex) {

						}
					}

				}
				watchKey.reset();
			}

		} catch (

		InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isFileExists(String filepath) {
		boolean exists = false;
		try {
			File file = new File(filepath);
			if (file.exists())
				exists = true;
		} catch (Exception ex) {
			log.error("error occured while checking if file:" + filepath + " exists because: " + ex.getMessage());
		}
		return exists;
	}

	public EAssessmentNoticeEntity convertReturnedAssessmentToEassesssmentEntity(EAssessmentNotice notice) {
		EAssessmentNoticeEntity assessmentNotice = null;
		try {
			 if(notice!=null) {
				 assessmentNotice = new EAssessmentNoticeEntity(notice);
			 }
		} catch (Exception ex) {
			log.error("error occured while getting Eassessment because: " + ex.getMessage());
		}
		return assessmentNotice;
	}

}
