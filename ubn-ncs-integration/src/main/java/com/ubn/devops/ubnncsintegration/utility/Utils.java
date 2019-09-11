package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class Utils {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private FilePathsConfig filePathConfig;

	@Autowired
	private WatchService watchService;

	@Autowired
	private PaymentDetailsService paymentDetailsService;

	public void watchFolder() {
		EAssessmentNotice eAssessmentNotice = null;
		String filename = null;
		try {
			WatchKey watchKey;
			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					filename = filePathConfig.getRootfolder() + event.context();
					FileReaderResponse frResponse = CustomMarshaller.readFile(filename);
					if (frResponse != null) {
						switch (frResponse.getClassName()) {
						case FileReaderResponse.EASSESSMENTNOTICE:
							eAssessmentNotice = (EAssessmentNotice) frResponse.getObject();
							if (eAssessmentNotice != null) {
								// Persist eAssessmentNotice to database
								PaymentDetails paymentDetails = paymentDetailsService
										.savePaymentDetails(eAssessmentNotice);
								if (paymentDetails != null) {
									moveFile(new File(filename), filePathConfig.getAssessmentnotice());
									// if the paymentdetails was successfully saved then go ahead and confirm that
									// the notice receipt
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
									int isResponseXmlCreated = CustomMarshaller.marshall(response,
											filePathConfig.getRootfolder());
									if (isResponseXmlCreated == 1) {
										log.info("created acknowledge response xml for declarant code: "
												+ response.getDeclarantCode());
										paymentDetailsService
												.acknowledgePaymentDetails(paymentDetails.getDeclarantCode());
									}
								}

							}
							break;
						case FileReaderResponse.EPAYMENTCONFIRMATION:
							String paymentResponsePath = filePathConfig.getRootfolder() + event.context();

							TransactionResponse response = (TransactionResponse) frResponse.getObject();
							if (response != null) {
								// update payment response
								//paymentDetailsService.updatePaymentDetailsWithResponse(response);
								moveFile(new File(paymentResponsePath), filePathConfig.getPaymentresponse());
							}
							break;
						}
					}

				}

				watchKey.reset();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * private boolean isFileExists(String filepath) { boolean exists = false; try {
	 * File file = new File(filepath); if (file.exists()) exists = true; } catch
	 * (Exception ex) { log.error("error occured while checking if file:" + filepath
	 * + " exists because: " + ex.getMessage()); } return exists; }
	 */

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

	private void moveFile(File theFile, String directory) {
		
		try {
			FileUtils.moveFileToDirectory(theFile, new File(directory), false);
		} catch (Exception ex) {
			log.error("Error occured while trying to move file" + theFile.getName() + " to " + directory + " because: "
					+ ex.getMessage());
		}
	}

}
