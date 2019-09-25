package com.ubn.devops.ubnncsintegration.utility;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;

public class Utils {

	private static  Logger log = LoggerFactory.getLogger(Utils.class);

	
	/*
	 * private boolean isFileExists(String filepath) { boolean exists = false; try {
	 * File file = new File(filepath); if (file.exists()) exists = true; } catch
	 * (Exception ex) { log.error("error occured while checking if file:" + filepath
	 * + " exists because: " + ex.getMessage()); } return exists; }
	 */

	public static PaymentDetails convertReturnedAssessmentToEassesssmentEntity(EAssessmentNotice notice) {
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

	public static void moveFile(File theFile, String directory) {
		try {
			FileUtils.moveFileToDirectory(theFile, new File(directory), false);
		} catch (Exception ex) {
			log.error("Error occured while trying to move file" + theFile.getName() + " to " + directory + " because: "
					+ ex.getMessage());
		}
	}

}

