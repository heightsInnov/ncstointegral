package com.ubn.devops.ubnncsintegration.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubn.devops.ubnncsintegration.model.EAssessmentNoticeEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.repository.EAssessmentNoticeRepository;
import com.ubn.devops.ubnncsintegration.repository.TaxEntityRepository;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.EAssessmentNoticeService;
import com.ubn.devops.ubnncsintegration.utility.CustomMarshaller;
import com.ubn.devops.ubnncsintegration.utility.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EAssessmentNoticeServiceImpl implements EAssessmentNoticeService{

	@Autowired
	private Utils utils;
	
	@Autowired
	private EAssessmentNoticeRepository noticeRepo;
	
	@Autowired
	private TaxEntityRepository taxRepo;
	
	@Override
	public EAssessmentNoticeEntity saveAssessmentNotice(EAssessmentNotice assessmentNotice) {
		EAssessmentNoticeEntity ass = null;
		try {
			EAssessmentNoticeEntity entity = utils.convertReturnedAssessmentToEassesssmentEntity(assessmentNotice);
			if(entity!=null) {
				if(!entity.getTaxes().isEmpty()) {
					taxRepo.saveAll(entity.getTaxes());
				}
				ass = noticeRepo.save(entity);
			} 
		}catch(Exception ex) {
			log.error("error occured while trying to save assessmentnotice entity because: "+ex.getMessage());
		}
		return ass;
	}

	@Override
	public EAssessmentNotice updateAssessmentNotice(EAssessmentNotice assessmentNotice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EAssessmentNotice> findAllEAssessmentNotices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse findEAssessmentNoticeFormMNumber(String formMNumber) {
		EAssessmentNoticeEntity notice = null;
		ApiResponse response = new ApiResponse(500,"Error occured while processing request. Please try again later.");
		try {
			notice = noticeRepo.findByFormMNumber(formMNumber);
			
			if(notice!=null) {
				//generate xml file
				EAssessmentNotice assessmentNotice = new EAssessmentNotice(notice);
				CustomMarshaller.marshall(assessmentNotice, "C:/Users/lababatunde/Desktop/NCS/");
				response.setCode(200);
				response.setMessage("Successful");
				response.setBody(notice);
			}else {
				response.setCode(404);
				response.setMessage("Not found");
			}
		}catch(Exception ex) {
			log.error("Error occured while finding EAssessmentNotice because: "+ex.getMessage());
		}
		return response;
	}

}
