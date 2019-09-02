package com.ubn.devops.ubnncsintegration.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.PaymentEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentQuery;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.repository.PaymentRepository;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentService;
import com.ubn.devops.ubnncsintegration.utility.CustomMarshaller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private FilePathsConfig pathsConfig;

	@Autowired
	private PaymentRepository paymentRepo;

	@Override
	public ApiResponse requestPayment(EPaymentConfirmation ePaymentConfirmation) {
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR,
				"Unable to process request right now, Please try again");
		try {
			if (ePaymentConfirmation != null) {
				int isMarshalled = CustomMarshaller.marshall(ePaymentConfirmation, pathsConfig.getPaymentrequest());
				if (isMarshalled == 1) {
					response.setCode(ApiResponse.SUCCESSFUL);
					response.setMessage("Successful");
					response.setBody(ePaymentConfirmation);
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to request payment because: " + ex.getMessage());
		}
		return response;
	}

	@Override
	public PaymentEntity savePaymentDetails(EPaymentConfirmation paymentConfirmation) {
		PaymentEntity entity = null;
		log.info("=======Trying to save payment details======");
		try {
			PaymentEntity convertedPaymentEntity = new PaymentEntity(paymentConfirmation);
			if (convertedPaymentEntity != null) {
				entity = paymentRepo.save(convertedPaymentEntity);
				CustomMarshaller.marshall(paymentConfirmation, pathsConfig.getPaymentrequest());
				log.info("Successfully saved payment details");
			}

		} catch (Exception ex) {
			log.error("Error occured while saving payment details because: " + ex.getMessage(), ex);
		}
		return entity;
	}

	@Override
	public void queryTransaction(EPaymentQuery query) {
		log.info("====Trying to query transaction details====");
		try {
			if(query!=null) {
				int isMarshalled = CustomMarshaller.marshall(query, pathsConfig.getQueryrequest());
				if(isMarshalled==1) {
					log.info("Successfully sent query request");
				}else {
					log.warn("Unable to send query request");
				}
			}else {
				log.warn("A null object was passed. Cannot send a null object as query request");
			}
		}catch(Exception ex) {
			log.error("Error occured while trying to query payment details because: "+ex.getMessage(),ex); 
		}
	}

}
