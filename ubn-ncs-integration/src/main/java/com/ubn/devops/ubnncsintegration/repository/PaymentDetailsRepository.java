package com.ubn.devops.ubnncsintegration.repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.ubn.devops.ubnncsintegration.mapper.CustomMapper;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.utility.Utils;

@Repository
public class PaymentDetailsRepository {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${db.schemaname}")
	private String SCHEMANAME;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private final String PACKAGENAME = "NCS_PACKAGE";

	@Autowired
	private Utils utils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public PaymentDetails downloadPaymentDetails(EAssessmentNotice assessmentNotice) {
		PaymentDetails paymentDetails = null;
		try {
			PaymentDetails payment = utils.convertReturnedAssessmentToEassesssmentEntity(assessmentNotice);
			if (payment != null) {
				SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
				call.setSchemaName(SCHEMANAME);
				call.setCatalogName(PACKAGENAME);
				call.setProcedureName("SAVEPAYMENTDETAILS");
				call.declareParameters(new SqlParameter("p_sadyear", Types.INTEGER),
						new SqlParameter("p_customscode", Types.VARCHAR),
						new SqlParameter("p_declarantcode", Types.VARCHAR),
						new SqlParameter("p_declarantName", Types.VARCHAR),
						new SqlParameter("p_sadassessmentserial", Types.VARCHAR),
						new SqlParameter("p_sadassessmentnumber", Types.VARCHAR),
						new SqlParameter("p_sadassessmentdate", Types.DATE),
						new SqlParameter("p_companycode", Types.VARCHAR),
						new SqlParameter("p_companyname", Types.VARCHAR), new SqlParameter("bankcode", Types.VARCHAR),
						new SqlParameter("bankbranchcode", Types.VARCHAR),
						new SqlParameter("p_formmnumber", Types.VARCHAR),
						new SqlParameter("p_totalamounttobepaid", Types.DECIMAL),
						new SqlOutParameter("p_responsecode", Types.VARCHAR),
						new SqlOutParameter("p_responsemsg", Types.VARCHAR),
						new SqlOutParameter("p_id", Types.DECIMAL));
				Map<String, Object> paramSource = new HashMap<>();
				paramSource.put("p_sadyear", payment.getSadYear());
				paramSource.put("p_customscode", payment.getCustomsCode());
				paramSource.put("p_declarantcode", payment.getDeclarantCode());
				paramSource.put("p_declarantName", payment.getDeclarantName());
				paramSource.put("p_sadassessmentserial", payment.getSadAssessmentSerial());
				paramSource.put("p_sadassessmentnumber", payment.getSadAssessmentNumber());
				paramSource.put("p_sadassessmentdate", payment.getSadAssessmentDate());
				paramSource.put("p_companycode", payment.getCompanyCode());
				paramSource.put("p_companyname", payment.getCompanyName());
				paramSource.put("bankcode", payment.getBankCode());
				paramSource.put("bankbranchcode", payment.getBankBranchCode());
				paramSource.put("p_formmnumber", payment.getFormMNumber());
				paramSource.put("p_totalamounttobepaid", payment.getTotalAmountToBePaid());
				Map<String, Object> respValues = call.execute(paramSource);

				if (!respValues.isEmpty()) {
					String responsecode = respValues.get("p_responsecode").toString();
					if (responsecode.equals("00")) {
						log.info("Successfully saved the payment details with declarant code: "
								+ assessmentNotice.getDeclarantCode());
						payment.setId(((BigDecimal) respValues.get("p_id")).longValue());
						paymentDetails = payment;
					} else if (responsecode.equals("99")) {
						log.warn("db error occured with message: " + respValues.get("p_responsemsg"));
					}
				}

			}
		} catch (Exception ex) {
			log.error(
					"error occured while trying to download payment details into database because: " + ex.getMessage(),
					ex);
		}
		return paymentDetails;
	}

	public void setPaymentDetailsAsAcknowledged(String formMNumber) {
		log.info("Trying to set transaction with declarantcode:" + formMNumber + " as acknowledge");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("ACKNOWLEDGERECEIPT");
			call.declareParameters(new SqlParameter("p_formM", Types.VARCHAR),
					new SqlOutParameter("p_responsemsg", Types.VARCHAR),
					new SqlOutParameter("p_responsecode", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("p_formM", formMNumber);
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				String responsecode = respValues.get("p_responsecode").toString();
				if (responsecode.equals("00")) {
					log.info("Successfully set the payment details with declarant code: " + formMNumber
							+ " as acknowledge");

				} else if (responsecode.equals("99")) {
					log.warn("db error occured with message: " + respValues.get("p_responsemsg"));
				}
			}

		} catch (Exception ex) {
			log.error("error occured while trying to set payment details as acknowledge becuase: " + ex.getMessage(),
					ex);
		}
	}

	@SuppressWarnings("unchecked")
	public PaymentDetails findPaymentDetails(String formMNumber) {
		PaymentDetails model = null;
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("FINDPAYMENTDETAILS");
			call.declareParameters(new SqlParameter("p_formM", Types.VARCHAR),
					new SqlOutParameter("p_responsemsg", Types.VARCHAR),
					new SqlOutParameter("p_responsecode", Types.VARCHAR),
					new SqlOutParameter("p_data", Types.REF_CURSOR));
			call.addDeclaredRowMapper("p_data", new CustomMapper());
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("p_formM", formMNumber);
			Map<String, Object> result = call.execute(paramSource);
			if (!result.isEmpty()) {
				String rspCode = result.get("p_responsecode").toString();
				if (rspCode.equals("00")) {
					if (result.get("p_data") != null) {
						List<PaymentDetails> payments = (List<PaymentDetails>) result.get("p_data");
						if (!payments.isEmpty()) {
							model = payments.get(0);
							log.info("Successfully found payment details");
						} else {
							log.warn("payment details with unique code: " + formMNumber + " does  not exist");
						}

					}
				} else if (rspCode.equals("99")) {
					log.error("db error occured with message: " + result.get("p_responsemsg"));
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to fetch payment details with unique code: " + formMNumber
					+ " because: " + ex.getMessage(), ex);
		}
		return model;
	}

	public int validateTransactionReference(String txnReference,String formMNumber) {
		int responseValue = 0;
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("VALIDATETRANSREFERENCE");
			call.declareParameters(new SqlParameter("p_txnreference", Types.VARCHAR),
					new SqlParameter("p_formmnumber", Types.VARCHAR),
					new SqlOutParameter("p_responsemsg", Types.VARCHAR),
					new SqlOutParameter("p_responsecode", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("p_formmnumber", formMNumber);
			paramSource.put("p_txnreference"
					, txnReference);
			Map<String, Object> result = call.execute(paramSource);
			if (!result.isEmpty()) {
				String rspCode = result.get("p_responsecode").toString();
				if (rspCode.equals("00")) {
					responseValue = 1;
				} else if (rspCode.equals("99")) {
					responseValue = 2;
					log.info("db error occured with msg: " + result.get("p_responsemsg").toString());
				} else if (rspCode.equals("11")) {
					responseValue = 3;
				}

			}

		} catch (Exception ex) {
			log.info("Error occured while trying validate transaction reference because: " + ex.getMessage());
		}
		return responseValue;
	}

	public int updateSuccessfulValidation(PaymentProcessRequest request) {
		int isUpdated = 0;
	
			log.info("Trying to update payment details After successful FCUBS ref validation with formMNumber:" + request.getFormMNumber() + " as acknowledge");
			try {
				SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
				call.setSchemaName(SCHEMANAME);
				call.setCatalogName(PACKAGENAME);
				call.setProcedureName("UPDATESUCCESSFULVALIDATION");
				call.declareParameters(new SqlParameter("P_FORMMNUMBER", Types.VARCHAR),
						new SqlParameter("P_TXNREF", Types.VARCHAR),
						new SqlParameter("P_ACCTNO", Types.VARCHAR),
						new SqlParameter("P_CUSTEMAIL", Types.VARCHAR),
						new SqlParameter("P_AMOUNTPAID", Types.DECIMAL),
						new SqlParameter("P_CHANNEL", Types.VARCHAR),
						new SqlParameter("P_PAYMENTDATE", Types.TIMESTAMP),
						new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR),
						new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR));
				Map<String, Object> paramSource = new HashMap<>();
				paramSource.put("P_FORMMNUMBER", request.getFormMNumber());
				paramSource.put("P_TXNREF", request.getExternalRef());
				paramSource.put("P_ACCTNO", request.getAccount());
				paramSource.put("P_CUSTEMAIL", request.getCustomerEmail());
				paramSource.put("P_AMOUNTPAID", new BigDecimal(request.getAmount()));
				paramSource.put("P_CHANNEL", request.getChannel());
				paramSource.put("P_PAYMENTDATE", sdf.format(request.getPostingDate()));
				Map<String, Object> respValues = call.execute(paramSource);
				if (!respValues.isEmpty()) {
					String responsecode = respValues.get("P_RESPONSECODE").toString();
					if (responsecode.equals("00")) {
						isUpdated =1;
						log.info("Successfully updated the payment details with formMNumber: " + request.getFormMNumber()
								+ " as acknowledge");

					} else if (responsecode.equals("99")) {
						log.warn("db error occured with message: " + respValues.get("P_RESPONSEMSG"));
					}
				}
		} catch (Exception ex) {
			log.info("error while updating details with payment info because: "+ex.getMessage(),ex);
		}
		return isUpdated;
	}
}
