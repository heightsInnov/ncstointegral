package com.ubn.devops.ubnncsintegration.service;

import java.util.List;

import com.ubn.devops.ubnncsintegration.model.EAssessmentNoticeEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;

public interface EAssessmentNoticeService {
	
	public EAssessmentNoticeEntity saveAssessmentNotice(EAssessmentNotice assessmentNotice);
	
	public EAssessmentNotice updateAssessmentNotice(EAssessmentNotice assessmentNotice);

	public List<EAssessmentNotice> findAllEAssessmentNotices();
	
	public ApiResponse findEAssessmentNoticeFormMNumber(String formMNumber);
}
