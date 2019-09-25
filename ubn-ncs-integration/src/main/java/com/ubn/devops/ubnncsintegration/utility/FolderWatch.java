package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;

@Component
public class FolderWatch {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FilePathsConfig config;

	@Autowired
	private WatchService watchService;

	@Autowired
	private PaymentDetailsService paymentDetailsService;	

	public void watchFolder() {
		String filename = null;
		try {
			WatchKey watchKey;
			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					Path dir = (Path) watchKey.watchable();
					filename = dir.resolve(event.context().toString()).toString();
					if (isFileExists(filename)) {
						FileReaderResponse readerResponse = CustomMarshaller.readFile(filename);
						if (readerResponse != null) {
							switch (readerResponse.getClassName()) {
							case FileReaderResponse.EASSESSMENTNOTICE:
								// Persist eAssessmentNotice to database
								EAssessmentNotice eAssessmentNotice = (EAssessmentNotice) readerResponse.getObject();
								eAssessmentNotice.setAssessmentFilename(event.context().toString());
								PaymentDetails paymentDetails = paymentDetailsService
										.savePaymentDetails(eAssessmentNotice);
								if (paymentDetails != null) {
									// paymentDetailsService.acknowledgePaymentDetails(paymentDetails.getFormMNumber());
									Utils.moveFile(new File(filename), config.getAssessmentnotice());
								}
								break;
							case FileReaderResponse.TRANSACTIONRESPONSE:
								TransactionResponse transactionResponse = (TransactionResponse) readerResponse
										.getObject();
								int isUpdated = paymentDetailsService.updatePaymentWithNCSResponse(transactionResponse);
								if(isUpdated==1) {
									paymentDetailsService.performSweeporRetract(transactionResponse);
									Utils.moveFile(new File(filename), config.getTransactionresponse());
								}
								break;
							}

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

}

