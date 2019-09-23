package com.ubn.devops.ubnncsintegration.serviceimpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.repository.PaymentDetailsRepository;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.response.PaymentDetailsResponse;
import com.ubn.devops.ubnncsintegration.service.PaymentDetailsService;
import com.ubn.devops.ubnncsintegration.sweep.SweepRequestProcessor;
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
	
	@Autowired
	private SweepRequestProcessor processor;

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


	
	@Override
	public ApiResponse fetchPaymentDetails(int sadYear,String customsCode,String sadAssessmentSerial
			,String sadAssessmentNumber,String version) {
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR,
				"Unable to process request right now. Please try again");
		log.info("======Trying to fetch payment details using assessmentNumber: " +sadAssessmentNumber + " and customsCode:"+customsCode+" and sadyear:"+sadYear+"======");
		try {
			PaymentDetails model = paymentRepo.findPaymentDetails(sadYear, customsCode, sadAssessmentSerial, sadAssessmentNumber, version);
			if (model != null) {
				if (model.getPaymentStatus().equals("0")) {
					response.setCode(ApiResponse.SUCCESSFUL);
					response.setMessage("Successful");
					PaymentDetailsResponse detailsResponse = new PaymentDetailsResponse();
					detailsResponse.setAmount(model.getTotalAmountToBePaid());
					detailsResponse.setCompanyName(model.getCompanyName());
					detailsResponse.setDeclarantCode(model.getDeclarantCode());
					detailsResponse.setDeclarantName(model.getDeclarantName());
					detailsResponse.setCustomsCode(model.getCustomsCode());
					detailsResponse.setSadYear(model.getSadYear());
					detailsResponse.setSadAssessmentNumber(model.getSadAssessmentNumber());
					detailsResponse.setSadAssessmentSerial(model.getSadAssessmentSerial());
					response.setBody(detailsResponse);
				} else {
					response.setCode(ApiResponse.ALREADY_PAID);
					response.setMessage("This Payment has already been made or in progress.");
				}
			} else {
				response.setCode(ApiResponse.NOT_FOUND);
				response.setMessage("Not found");
			}

		} catch (Exception ex) {
			log.error("Error occured while trying to fetch payment details with assessmentNumber: " +sadAssessmentNumber + " and customsCode:"+customsCode+" and sadyear:"+sadYear+" because: " + ex.getMessage(), ex);
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
			PaymentDetails details = paymentRepo.findPaymentDetails(request.getSadYear(),request.getCustomsCode(),request.getSadAssessmentSerial(),request.getSadAssessmentNumber(),request.getVersion());
			if (details != null) {
				if (!details.getPaymentStatus().equals(PaymentDetails.PENDING)) {
					response.setCode(ApiResponse.ALREADY_PAID);
					response.setMessage("This payment has already been made");
				} else {
					int respValue = paymentRepo.validateTransactionReference(request);
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
								confirmation.setPaymentDate(details.getPostingDate());
								List<String> folders = Arrays.asList(pathConfig.getInfolder(),pathConfig.getPaymentrequest());
								CustomMarshaller.marshall(confirmation, folders,FileReaderResponse.EPAYMENTCONFIRMATION);
								response.setCode(ApiResponse.SUCCESSFUL);
								response.setMessage("Successfully processed payment. Response will be sent to you shortly");
							}

						} else if (status == 99 || status == 1) {
			
							log.error("Invalid reference");
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

	/*
	 * @Override public ApiResponse queryPaymentDetails(EPaymentQuery paymentQuery)
	 * { ApiResponse response = new
	 * ApiResponse(ApiResponse.SERVER_ERROR,"Unable to process request right now");
	 * log.info("querying the TWM server for payment details"); try {
	 * if(paymentQuery!=null) { int isMarshalled
	 * =CustomMarshaller.marshall(paymentQuery, pathConfig.getRootfolder(),
	 * FileReaderResponse.EPAYMENTQUERY); if(isMarshalled==1) {
	 * log.info("successfully sent the query");
	 * response.setCode(ApiResponse.SUCCESSFUL);
	 * response.setMessage("Successfully sent epayment query request"); } }
	 * }catch(Exception ex) {
	 * log.error("Error occured while processing request because :"+ex.getMessage(),
	 * ex); } return response; }
	 */


	@Override
	public int updatePaymentWithNCSResponse(TransactionResponse response) {	
		
		return paymentRepo.updatePaymentWithNCSResponse(response);
	}



	@Override
	public String performSweeporRetract(TransactionResponse response) {
		String rsp = null;
		try {
			SadAsmt sadAsmt = response.getSadAsmt();
			PaymentDetails details = paymentRepo.findPaymentDetails(sadAsmt.getSadYear(),response.getCustomsCode(), sadAsmt.getSadAssessmentSerial(), sadAsmt.getSadAssessmentNumber(), response.getVersion());
			if(details!=null) {
				if(details.getPaymentStatus().equals(PaymentDetails.PAYED))
				   rsp = processor.DoSweepPostingProcess(details.getFcubsPostingRef(), response.getTransactionStatus().value());
			}
		}catch(Exception ex) {
			log.error("Error occured while performing sweep or retract function for response:"+response.toString()+" because: "+ex.getMessage(),ex);
		}
		return rsp;
	}

}
