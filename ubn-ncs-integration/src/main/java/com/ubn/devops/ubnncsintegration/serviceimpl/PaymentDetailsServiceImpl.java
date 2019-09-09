package com.ubn.devops.ubnncsintegration.serviceimpl;

import java.math.BigDecimal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.Payment;
import com.ubn.devops.ubnncsintegration.ncsschema.SadAsmt;
import com.ubn.devops.ubnncsintegration.repository.PaymentDetailsRepository;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.response.PaymentDetailsResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;
import com.ubn.devops.ubnncsintegration.utility.CustomMarshaller;
import com.ubn.devops.ubnncsintegration.utility.FileReaderResponse;
import com.ubn.devops.ubnncsintegration.utility.RestService;

@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PaymentDetailsRepository paymentRepo;

	@Autowired
	private RestService restService;
	
	@Autowired
	private FilePathsConfig pathConfig; 

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
	 * declarantCode + " ======="); try { PaymentDetailsResponse paymentModel =
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
	public ApiResponse fetchPaymentDetails(String formMNumber) {
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR,
				"Error occured while fetching details by declarant code.");
		log.info("======Trying to fetch payment details using declarantcode: " + formMNumber + "======");
		try {
			PaymentDetails model = paymentRepo.findPaymentDetails(formMNumber);
			if (model != null) {
				if (!model.isPaid()) {
					response.setCode(ApiResponse.SUCCESSFUL);
					response.setMessage("Successful");
					PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
					detailsResponse.setAmount(model.getTotalAmountToBePaid());
					detailsResponse.setCompanyName(model.getCompanyName());
					detailsResponse.setFormMNumber(model.getFormMNumber());
					detailsResponse.setDeclarantCode(model.getDeclarantCode());
					detailsResponse.setDeclarantName(model.getDeclarantName());
					response.setBody(detailsResponse);
				} else {
					response.setCode(ApiResponse.ALREADY_PAID);
					response.setMessage("This Payment has already been made.");
				}
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
	public void acknowledgePaymentDetails(String formMNumber) {

		paymentRepo.setPaymentDetailsAsAcknowledged(formMNumber);

	}

	@Override
	public ApiResponse processPayment(PaymentProcessRequest request) {
		log.info("processing payment request with details: " + request.toString());
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR, "Unable to process request. Please try again");
		try {
			PaymentDetails details = paymentRepo.findPaymentDetails(request.getFormMNumber());
			if (details != null) {
				if (details.isPaid()) {
					response.setCode(ApiResponse.ALREADY_PAID);
					response.setMessage("This payment has already been made");
				} else {
					int respValue = paymentRepo.validateTransactionReference(request.getExternalRef(),request.getFormMNumber());
					if (respValue == 1) {
						response.setCode(ApiResponse.ALREADY_EXIST);
						response.setMessage("This Reference has already been used.");
					} else if (respValue == 3) {
						// validate reference on fcubs
						int status = restService.checkValidation(request);
						if (status == 0) {
							// The reference is valid on fcubs
							int isUpdated = paymentRepo.updateSuccessfulValidation(request);
							if (isUpdated == 1) {
								//Build The transaction response
								EPaymentConfirmation confirmation = new EPaymentConfirmation();
								confirmation.setBankCode(details.getBankCode());
								confirmation.setCustomsCode(details.getCustomsCode());
								confirmation.setDeclarantCode(details.getDeclarantCode());
								Payment payment = new Payment();
								payment.setAmount(new BigDecimal(request.getAmount()));
								payment.setMeansOfPayment(request.getChannel());
								payment.setReference(request.getExternalRef());
								confirmation.setPayment(Arrays.asList(payment));
								SadAsmt sadAsmt = new SadAsmt();
								sadAsmt.setSadAssessmentNumber(details.getSadAssessmentNumber());
								sadAsmt.setSadAssessmentSerial(details.getSadAssessmentSerial());
								sadAsmt.setSadYear(details.getSadYear());
								confirmation.setSadAsmt(sadAsmt);
								confirmation.setTotalAmountToBePaid(details.getTotalAmountToBePaid());
								CustomMarshaller.marshall(confirmation, pathConfig.getRootfolder(),FileReaderResponse.EPAYMENTCONFIRMATION);
							}

						} else if (status == 99 || status == 1) {
							log.info("the reference " + request.getExternalRef() + " entered   ");
							response.setCode(ApiResponse.INVALID_REFERENCE);
							response.setMessage("Reference invalid");
						}
					}

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
