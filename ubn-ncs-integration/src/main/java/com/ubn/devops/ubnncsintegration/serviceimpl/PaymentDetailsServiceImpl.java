package com.ubn.devops.ubnncsintegration.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.repository.PaymentDetailsRepository;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

	@Autowired
	private PaymentDetailsRepository paymentRepo;

	@Override
	public PaymentDetails savePaymentDetails(EAssessmentNotice assessmentNotice) {
		PaymentDetails paymentDetails = null;
		log.info("=====Trying to save new payment details ===========");
		try {
			paymentDetails = paymentRepo.downloadPaymentDetails(assessmentNotice);

		} catch (Exception ex) {
			log.error("error occured while trying to save assessmentnotice entity because: " + ex.getMessage());
		}
		return paymentDetails;
	}

	/*
	 * @Override public void updatePaymentDetailsWithResponse(TransactionResponse
	 * response) { try {
	 * 
	 * String declarantCode = response.getDeclarantCode();
	 * log.info("====Trying to update payment details with declarant code:" +
	 * declarantCode + " ======="); try { PaymentDetails paymentModel =
	 * paymentRepo.findByDeclarantCode(declarantCode); if (paymentModel != null) {
	 * String rspMessage = "";
	 * paymentModel.setTransactionstatus(response.getTransactionStatus().value());
	 * Info info = response.getInfo(); if (info != null) { if
	 * (!info.getMessage().isEmpty()) { rspMessage =
	 * StringUtils.collectionToCommaDelimitedString(info.getMessage()); } }
	 * paymentModel.setResponseMessage(rspMessage); paymentRepo.save(paymentModel);
	 * log.info("successfully updated the payment object with declarant code: "
	 * +declarantCode+" with transaction response"); } else {
	 * 
	 * log.warn("======The declarant code:" + declarantCode + " does not exist"); }
	 * 
	 * } catch (Exception ex) {
	 * log.error("error occured while trying to update payment details because: " +
	 * ex.getMessage(), ex); } }
	 */
	@Override
	public ApiResponse fetchPaymentDetails(String declarantCode) {
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR,
				"Error occured while fetching details by declarant code.");
		log.info("======Trying to fetch payment details using declarantcode: " + declarantCode + "======");
		try {
			PaymentDetails model = paymentRepo.findPaymentDetails(declarantCode);
			if (model != null) {
				response.setCode(ApiResponse.SUCCESSFUL);
				response.setMessage("Successful");
				response.setBody(model);
			} else {
				response.setCode(ApiResponse.NOT_FOUND);
				response.setMessage("Not found");
			}

		} catch (Exception ex) {
			log.error("Error occured while trying to fetch payment details because: " + ex.getMessage(), ex);
		}
		return response;

	}

	@Override
	public void acknowledgePaymentDetails(String declarantCode) {

		paymentRepo.setPaymentDetailsAsAcknowledged(declarantCode);

	}

	@Override
	public ApiResponse processPayment(PaymentProcessRequest paymentProcessRequest) {
		log.info("processing payment request with details: " + paymentProcessRequest.toString());
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR, "Unable to process request. Please try again");
		try {
			PaymentDetails details = paymentRepo.findPaymentDetails(paymentProcessRequest.getDeclarantCode());
			if (details != null) {
				if (details.isPaid()) {
					response.setCode(ApiResponse.ALREADY_PAID);
					response.setMessage("This payment has already been made");
				} else {

				}
			} else {
				response.setCode(ApiResponse.NOT_FOUND);
				response.setMessage("The payment with this details does not exist");
			}

		} catch (Exception ex) {
			log.error("error occured while processing payment request because: " + ex.getMessage(), ex);
		}
		return response;
	}

}
