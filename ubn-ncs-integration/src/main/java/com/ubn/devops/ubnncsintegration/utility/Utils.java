package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.Info;
import com.ubn.devops.ubnncsintegration.ncsschema.SadAsmt;
import com.ubn.devops.ubnncsintegration.ncsschema.TRS;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Utils {

	@Autowired
	private FilePathsConfig filePathConfig;

	@Autowired
	private WatchService watchService;
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;

	public void watchFolder() {
		EAssessmentNotice eAssessmentNotice = null;
		String assessmentPath = null;
		String paymentResponsePath = null;
		File assessmentXmlFile = null;
		File paymentResponseFile = null;
		try {
			WatchKey watchKey;
			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					assessmentPath = filePathConfig.getAssessmentnotice() + event.context();
					if (isFileExists(assessmentPath)) {
						assessmentXmlFile = new File(assessmentPath);
						eAssessmentNotice = CustomMarshaller.unmarshall(assessmentXmlFile, EAssessmentNotice.class);
						if (eAssessmentNotice != null) {
							// Persist eAssessmentNotice to database
							PaymentDetails paymentDetails = paymentDetailsService.savePaymentDetails(eAssessmentNotice);
							if(paymentDetails!=null) {
								//if the paymentdetails was successfully saved then go ahead and confirm that the notice receipt
								TransactionResponse response = new TransactionResponse();
								response.setCustomsCode(paymentDetails.getCustomsCode());
								response.setDeclarantCode(paymentDetails.getDeclarantCode());
								response.setTransactionStatus(TRS.OK);
								Info info = new Info();
								info.setMessage(Arrays.asList("Successfully received the assessment notice"));
								response.setInfo(info);
								SadAsmt sadAsmt = new SadAsmt();
								sadAsmt.setSadAssessmentNumber(paymentDetails.getSadAssessmentNumber());
								sadAsmt.setSadAssessmentSerial(paymentDetails.getSadAssessmentSerial());
								sadAsmt.setSadYear(paymentDetails.getSadYear());
								response.setSadAsmt(sadAsmt);
								// create xml of transaction response in the transaction response folder
								int isResponseXmlCreated = CustomMarshaller.marshall(response, filePathConfig.getTransactionresponse());
								if(isResponseXmlCreated==1) {
									paymentDetailsService.acknowledgePaymentDetails(paymentDetails.getDeclarantCode());
								}
							}
			
						}
					}
					paymentResponsePath = filePathConfig.getPaymentresponse() + event.context();
					if (isFileExists(paymentResponsePath)) {
						paymentResponseFile = new File(paymentResponsePath);

						// read paymentresponse file
						TransactionResponse response = CustomMarshaller.unmarshall(paymentResponseFile,
								TransactionResponse.class);
						if (response != null) {
							// update payment response
							paymentDetailsService.updatePaymentDetailsWithResponse(response);
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

	public PaymentDetails convertReturnedAssessmentToEassesssmentEntity(EAssessmentNotice notice) {
		PaymentDetails assessmentNotice = null;
		try {
			if (notice != null) {
				assessmentNotice = new PaymentDetails(notice);
			}
		} catch (Exception ex) {
			log.error("error occured while getting Eassessment because: " + ex.getMessage());
		}
		return assessmentNotice;
	}

}
