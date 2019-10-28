package com.ubn.devops.ubnncsintegration.serviceimpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentResponse;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.Payment;
import com.ubn.devops.ubnncsintegration.ncsschema.SadAsmt;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.repository.PaymentDetailsRepository;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.response.ApiResponse;
import com.ubn.devops.ubnncsintegration.response.FcubsValidationResponse;
import com.ubn.devops.ubnncsintegration.response.PaymentDetailsResponse;
import com.ubn.devops.ubnncsintegration.response.StatusResponse;
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
	public ApiResponse fetchPaymentDetails(int sadYear, String customsCode, String sadAssessmentSerial,
			String sadAssessmentNumber) {
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR,
				"Unable to process request right now. Please try again");
		log.info("======Trying to fetch payment details using assessmentNumber: " + sadAssessmentNumber
				+ " and customsCode:" + customsCode + " and sadyear:" + sadYear + "======");
		try {
			PaymentDetails model = paymentRepo.findPaymentDetails(sadYear, customsCode, sadAssessmentSerial,
					sadAssessmentNumber);
			if (model != null) {
				if (model.getPaymentStatus().equals(PaymentDetails.PENDING)) {
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
					detailsResponse.setUniqueId(model.getUniqueId());
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
			log.error("Error occured while trying to fetch payment details with assessmentNumber: "
					+ sadAssessmentNumber + " and customsCode:" + customsCode + " and sadyear:" + sadYear + " because: "
					+ ex.getMessage(), ex);
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
			PaymentDetails details = paymentRepo.findByUniqueId(request.getUniqueId());
			if (details != null) {
				if (!details.getPaymentStatus().equals(PaymentDetails.PENDING)) {
					response.setCode(ApiResponse.ALREADY_PAID);
					response.setMessage("This payment has already been made");
				} else {
					int status = 99;
					int respValue = paymentRepo.validateTransactionReference(request);
					if (respValue == 1) {
						response.setCode(ApiResponse.ALREADY_EXIST);
						response.setMessage("Invalid/Used reference supplied.");
					} else if (respValue == 3) {
						// validate reference on fcubs
						FcubsValidationResponse rsp = restService.validateTransactionReference(request);
						if (rsp != null) {
							status = rsp.getStatus();
							if (status == 0) {
								// The reference is valid on fcubs
								int isUpdated = paymentRepo.updateSuccessfulValidation(request, rsp.getStatus(),
										rsp.getMessage());
								if (isUpdated == 1) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
									// Build The transaction response
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
									confirmation.setPaymentDate(sdf.format(request.getPostingDate()));
									confirmation.setVersion(details.getVersion());
									List<String> folders = Arrays.asList(pathConfig.getInfolder(),
											pathConfig.getPaymentrequest());
									CustomMarshaller.marshall(confirmation, folders,
											FileReaderResponse.EPAYMENTCONFIRMATION);
									response.setCode(ApiResponse.SUCCESSFUL);
									response.setMessage(
											"Successfully processed payment. Response will be sent to you shortly");
								}

							} else if (status == 99 || status == 1) {

								log.error("Invalid reference");
								response.setCode(ApiResponse.INVALID_REFERENCE);
								response.setMessage("Reference invalid");
							}
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

	@Override
	public int updatePaymentWithNCSResponse(TransactionResponse response) {

		return paymentRepo.updatePaymentWithNCSResponse(response);
	}

	@Override
	public String performSweeporRetract(String reference, String ncsTransactionStatus) {
		String resp = null;
		SweepPaymentResponse rsp = null;
		try {
			rsp = processor.DoSweepPostingProcess(reference, ncsTransactionStatus.toUpperCase());
			// now update the payment details with response from sweeporreversed function
			// code
			if (rsp != null) {
				paymentRepo.updateWithSweepReverseResponse(rsp.getUpdate_response(), reference);
			}
		} catch (Exception ex) {
			log.error("Error occured while performing sweep or retract function for response:" + rsp.toString()
					+ " because: " + ex.getMessage(), ex);
		}
		return resp;
	}

	@Override
	public ApiResponse getStatus(String uniqueId) {
		ApiResponse response = new ApiResponse(ApiResponse.SERVER_ERROR,
				"Unable to get status right now. Please try again later");
		try {
			StatusResponse status = paymentRepo.getStatus(uniqueId);
			if (status != null) {
				if (!status.getStatusCode().equals("404")) {
					response.setCode(ApiResponse.SUCCESSFUL);
					response.setMessage("Successful");
					response.setBody(status);
				} else {
					response.setCode(ApiResponse.NOT_FOUND);
					response.setMessage("Invalid data supplied.");
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to get status using unique Id: " + uniqueId + " because: "
					+ ex.getMessage(), ex);
		}
		return response;
	}

	@Override
	public List<PaymentDetails> findAllPaidAndNcsConfirmed() {
		log.info("==Trying to find all paid and ncs confirmed details======");

		return paymentRepo.findAllPayedAndNcsConfirmed();
	}

}
