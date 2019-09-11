package com.ubn.devops.ubnncsintegration.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
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
import com.ubn.devops.ubnncsintegration.model.SweepPaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPersistAgent;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.utility.Utils;

@Repository
public class PaymentDetailsRepository {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${db.schemaname}")
	private String SCHEMANAME;

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

	public void setPaymentDetailsAsAcknowledged(String declarantCode) {
		log.info("Trying to set transaction with declarantcode:" + declarantCode + " as acknowledge");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("ACKNOWLEDGERECEIPT");
			call.declareParameters(new SqlParameter("p_declarantcode", Types.VARCHAR),
					new SqlOutParameter("p_responsemsg", Types.VARCHAR),
					new SqlOutParameter("p_responsecode", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("p_declarantcode", declarantCode);
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				String responsecode = respValues.get("p_responsecode").toString();
				if (responsecode.equals("00")) {
					log.info("Successfully set the payment details with declarant code: " + declarantCode
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
	public PaymentDetails findPaymentDetails(String declarantcode) {
		PaymentDetails model = null;
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("FINDPAYMENTDETAILS");
			call.declareParameters(new SqlParameter("p_declarantcode", Types.VARCHAR),
					new SqlOutParameter("p_responsemsg", Types.VARCHAR),
					new SqlOutParameter("p_responsecode", Types.VARCHAR),
					new SqlOutParameter("p_data", Types.REF_CURSOR));
			call.addDeclaredRowMapper("p_data", new CustomMapper());
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("p_declarantcode", declarantcode);
			Map<String, Object> result = call.execute(paramSource);
			if (!result.isEmpty()) {
				String rspCode = result.get("p_responsecode").toString();
				if (rspCode.equals("00")) {
					if (result.get("p_data") != null) {
						List<PaymentDetails> payments = (List<PaymentDetails>) result.get("p_data");
						if (!payments.isEmpty()) {
							model = payments.get(0);
						}
						log.info("Successfully found payment details");
					} else {
						log.warn("payment details with declarant code: " + declarantcode + " does  not exist");
					}
				} else if (rspCode.equals("99")) {
					log.error("db error occured with message: " + result.get("p_responsemsg"));
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to fetch payment details with declarant code: " + declarantcode
					+ " because: " + ex.getMessage(), ex);
		}
		return model;
	}

	public List<SweepPaymentDetails> findFCUBSDetails(String reference, String code) {
		List<SweepPaymentDetails> models = new ArrayList<>();
		SweepPaymentDetails model = null;
		ResultSet rs = null;
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("GETSWEEPPAYMENTDETAILS");
			call.declareParameters(new SqlOutParameter("RESPONSE", Types.VARCHAR),
					new SqlOutParameter("PAYMENTDETAILS", Types.REF_CURSOR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("paymentref", reference);
			paramSource.put("task_code", code);
			Map<String, Object> result = call.execute(paramSource);
			if (!result.isEmpty()) {
				String rspCode = result.get("RESPONSE").toString();
				if (rspCode.equals("00")) {
					if (result.get("PAYMENTDETAILS") != null) {
						rs = (ResultSet) result.get("PAYMENTDETAILS");
						while (rs.next()) {
							model = new SweepPaymentDetails();
							model.setAc_branch(rs.getString("AC_BRANCH"));
							model.setAc_no(rs.getString("AC_NO"));
							model.setAc_ccy(rs.getString("AC_CCY"));
							model.setTrn_code(rs.getString("TRN_CODE"));
							model.setLcy_amount(rs.getString("AMOUNT"));
							model.setCust_gl(rs.getString("CUST_GL"));
							model.setExternal_ref_no(rs.getString("EXTERNAL_REF_NO"));
							model.setDrcr_ind(rs.getString("DRCR_IND"));
							model.setFormmnumber(rs.getString("FORMMNUMBER"));
							model.setTask_code(code);
							
							models.add(model);
						}
						log.info("Successfully found sweep payment details");
					} else {
						log.warn("sweep payment details with payment reference: " + reference + " does  not exist");
					}
				} else if (rspCode.equals("99")) {
					log.error("db error occured!");
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to fetch payment details with declarant code: " + reference
					+ " because: " + ex.getMessage(), ex);
		}
		return models;
	}

	public void persistSweepInfo(SweepPersistAgent sweepAgent) {
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("PERSISTSWEEPDATA");
			call.declareParameters(new SqlOutParameter("respcode", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("RECORD_ID", sweepAgent.getRecordid());
			paramSource.put("BATCH_ID", sweepAgent.getBatchid());
			paramSource.put("INITIATING_BRANCH", sweepAgent.getInitiatingbranch());
			paramSource.put("REQUEST_MODULE", sweepAgent.getRequestmodule());
			paramSource.put("MODULE_CREDENTIALS", sweepAgent.getModulecredentials());
			paramSource.put("CURRENCY", sweepAgent.getTransactioncurrency());
			paramSource.put("DEBITACC", sweepAgent.getDebitacccountnumber());
			paramSource.put("CREDITACC", sweepAgent.getCreditacccountnumber());
			paramSource.put("BRANCH_CODE", sweepAgent.getBranchcode());
			paramSource.put("SRAMOUNT", sweepAgent.getAmount());
			paramSource.put("DEBITCREDITIND", sweepAgent.getDebitcreditindicator());
			paramSource.put("GLCASAIND", sweepAgent.getGlcasaindicator());
			paramSource.put("NEMONIC", sweepAgent.getMnemonic());
			paramSource.put("NARATION", sweepAgent.getNarration());
			paramSource.put("NCSREFERENCE", sweepAgent.getNcscustomreference());
			paramSource.put("PAYMENT_REFERENCE", sweepAgent.getPaymentreference());
			paramSource.put("TRANS_ID", sweepAgent.getTransactionid());
			paramSource.put("RESP_CODE", sweepAgent.getResponsecode());
			paramSource.put("RESP_MESG", sweepAgent.getResponsemessage());
			paramSource.put("RESPONSE_TIME", sweepAgent.getResponsetime());
			paramSource.put("UIDSS", sweepAgent.getUids());
			paramSource.put("BATCHDETAILID", sweepAgent.getBatch_detail_id());
			paramSource.put("PAYMENTTYPEID", sweepAgent.getPayment_type_id());
			paramSource.put("SWEEPSTATUS", sweepAgent.getIssweeporreversal());
			paramSource.put("NCSSTATUS", sweepAgent.getNcstransstatus());
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				String responsecode = respValues.get("p_responsecode").toString();
				if (responsecode.equals("00")) {
					log.info("Successfully set the payment details with declarant code: "
							+ sweepAgent.getNcscustomreference() + " as acknowledge");

				} else if (responsecode.equals("99")) {
					log.warn("db error occured with message: " + respValues.get("p_responsemsg"));
				}
			}

		} catch (Exception ex) {
			log.error("error occured while trying to set payment details as acknowledge becuase: " + ex.getMessage(),
					ex);
		}
	}
}
