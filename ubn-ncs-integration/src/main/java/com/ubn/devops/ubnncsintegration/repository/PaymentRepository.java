package com.ubn.devops.ubnncsintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubn.devops.ubnncsintegration.model.PaymentModel;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel,Long> {
	
	public PaymentModel findByDeclarantCode(String declarantCode);

}
