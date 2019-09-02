package com.ubn.devops.ubnncsintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubn.devops.ubnncsintegration.model.EAssessmentNoticeEntity;

@Repository
public interface EAssessmentNoticeRepository extends JpaRepository<EAssessmentNoticeEntity,Long> {

	public EAssessmentNoticeEntity findByFormMNumber(String formMNumber);

}
