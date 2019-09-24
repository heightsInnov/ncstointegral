package com.ubn.devops.ubnncsintegration.repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.ubn.devops.ubnncsintegration.mapper.PaymentDetailsMapper;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentResponse;
import com.ubn.devops.ubnncsintegration.model.SweepPersistAgent;
import com.ubn.devops.ubnncsintegration.model.TaxEntity;
import com.ubn.devops.ubnncsintegration.ncsschema.EAssessmentNotice;
import com.ubn.devops.ubnncsintegration.ncsschema.TransactionResponse;
import com.ubn.devops.ubnncsintegration.request.PaymentProcessRequest;
import com.ubn.devops.ubnncsintegration.utility.PropsReader;
import com.ubn.devops.ubnncsintegration.utility.Utils;

@Repository
public class PaymentDetailsRepository {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private String SCHEMANAME = PropsReader.getValue("db.schemaname");

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private final String PACKAGENAME = PropsReader.getValue("db.packagename");


	@Autowired
	private JdbcTemplate jdbcTemplate;

	public PaymentDetails downloadPaymentDetails(EAssessmentNotice assessmentNotice) {
		PaymentDetails paymentDetails = null;
		try {
			PaymentDetails payment = Utils.convertReturnedAssessmentToEassesssmentEntity(assessmentNotice);
			if (payment != null) {
				SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
				call.setSchemaName(SCHEMANAME);
				call.setCatalogName(PACKAGENAME);
				call.setProcedureName("SAVEPAYMENTDETAILS");
				call.declareParameters(new SqlParameter("P_SADYEAR", Types.INTEGER),
						new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
						new SqlParameter("P_DECLARANTCODE", Types.VARCHAR),
						new SqlParameter("P_DECLARANTNAME", Types.VARCHAR),
						new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
						new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
						new SqlParameter("P_SADASSESSMENTDATE", Types.DATE),
						new SqlParameter("P_COMPANYCODE", Types.VARCHAR),
						new SqlParameter("P_COMPANYNAME", Types.VARCHAR), new SqlParameter("BANKCODE", Types.VARCHAR),
						new SqlParameter("BANKBRANCHCODE", Types.VARCHAR),
						new SqlParameter("P_FORMMNUMBER", Types.VARCHAR),
						new SqlParameter("P_TOTALAMOUNTTOBEPAID", Types.DECIMAL),
						new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
						new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR), new SqlOutParameter("P_ID", Types.DECIMAL),
						new SqlParameter("P_VERSION", Types.VARCHAR)
						,new SqlParameter("P_FILENAME",Types.VARCHAR));
				Map<String, Object> paramSource = new HashMap<>();
				paramSource.put("P_SADYEAR", payment.getSadYear());
				paramSource.put("P_CUSTOMSCODE", payment.getCustomsCode());
				paramSource.put("P_DECLARANTCODE", payment.getDeclarantCode());
				paramSource.put("P_DECLARANTNAME", payment.getDeclarantName());
				paramSource.put("P_SADASSESSMENTSERIAL", payment.getSadAssessmentSerial());
				paramSource.put("P_SADASSESSMENTNUMBER", payment.getSadAssessmentNumber());
				paramSource.put("P_SADASSESSMENTDATE", payment.getSadAssessmentDate());
				paramSource.put("P_COMPANYCODE", payment.getCompanyCode());
				paramSource.put("P_COMPANYNAME", payment.getCompanyName());
				paramSource.put("BANKCODE", payment.getBankCode());
				paramSource.put("BANKBRANCHCODE", payment.getBankBranchCode());
				paramSource.put("P_FORMMNUMBER", payment.getFormMNumber());
				paramSource.put("P_TOTALAMOUNTTOBEPAID", payment.getTotalAmountToBePaid());
				paramSource.put("P_VERSION", payment.getVersion());
				paramSource.put("P_FILENAME", payment.getAssessmentFilename());
				Map<String, Object> respValues = call.execute(paramSource);
				if (!respValues.isEmpty()) {
					String responsecode = respValues.get("P_RESPONSECODE").toString();
					if (responsecode.equals("00")) {
						log.info("Successfully saved the payment details with declarant code: "
								+ assessmentNotice.getDeclarantCode());
						payment.setId(((BigDecimal) respValues.get("P_ID")).longValue());
						for (TaxEntity tax : payment.getTaxes()) {
							tax.setPaymentDetailsId(payment.getId());
							saveTax(tax);
						}
						paymentDetails = payment;
					} else if (responsecode.equals("99")) {
						log.warn("db error occured with message: " + respValues.get("P_RESPONSEMSG"));
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
	public PaymentDetails findPaymentDetails(int sadYear,String customsCode,String sadAssessmentSerial
					,String sadAssessmentNumber,String version) {

		PaymentDetails model = null;
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("FINDPAYMENTDETAILS");
			call.declareParameters(new SqlParameter("P_SADYEAR", Types.INTEGER),
					new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
					new SqlParameter("P_VERSION", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
					new SqlOutParameter("P_DATA", Types.REF_CURSOR));
			call.addDeclaredRowMapper("P_DATA", new PaymentDetailsMapper());
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_SADYEAR", sadYear);
			paramSource.put("P_CUSTOMSCODE", customsCode);
			paramSource.put("P_SADASSESSMENTSERIAL", sadAssessmentSerial);
			paramSource.put("P_SADASSESSMENTNUMBER", sadAssessmentNumber);
			paramSource.put("P_VERSION", version);
			Map<String, Object> result = call.execute(paramSource);
			if (!result.isEmpty()) {
				String rspCode = result.get("P_RESPONSECODE").toString();
				if (rspCode.equals("00")) {
					if (result.get("P_DATA") != null) {
						List<PaymentDetails> payments = (List<PaymentDetails>) result.get("P_DATA");
						if (!payments.isEmpty()) {
							model = payments.get(0);
							log.info("Successfully found payment details");
						} else {
							log.warn("payment details does  not exist");
						}
					}
				} else if (rspCode.equals("99")) {
					log.error("db error occured with message: " + result.get("P_RESPONSEMSG"));
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to fetch payment details with assessmentNumber:"+sadAssessmentNumber+" because: " + ex.getMessage(), ex);
		}
		return model;
	}

	public int validateTransactionReference(PaymentProcessRequest request) {
		int responseValue = 0;

		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("VALIDATETRANSREFERENCE");
			call.declareParameters(new SqlParameter("P_TXNREFERENCE", Types.VARCHAR),
					new SqlParameter("P_SADYEAR", Types.INTEGER),
					new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
					new SqlParameter("P_VERSION", Types.VARCHAR),
					new SqlOutParameter("p_responsemsg", Types.VARCHAR),
					new SqlOutParameter("p_responsecode", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_TXNREFERENCE", request.getExternalRef());
			paramSource.put("P_SADYEAR", request.getSadYear());
			paramSource.put("P_CUSTOMSCODE", request.getCustomsCode());
			paramSource.put("P_SADASSESSMENTSERIAL", request.getSadAssessmentSerial());
			paramSource.put("P_SADASSESSMENTNUMBER", request.getSadAssessmentNumber());
			paramSource.put("P_VERSION", request.getVersion());
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

		log.info("Trying to update payment details After successful FCUBS ref validation with formMNumber:"
				+ request.toString() + " as acknowledge");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("UPDATESUCCESSFULVALIDATION");
			call.declareParameters(new SqlParameter("P_SADYEAR", Types.INTEGER),
					new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
					new SqlParameter("P_VERSION", Types.VARCHAR), new SqlParameter("P_TXNREF", Types.VARCHAR),
					new SqlParameter("P_ACCTNO", Types.VARCHAR), new SqlParameter("P_CUSTEMAIL", Types.VARCHAR),
					new SqlParameter("P_AMOUNTPAID", Types.DECIMAL), new SqlParameter("P_CHANNEL", Types.VARCHAR),
					new SqlParameter("P_PAYMENTDATE", Types.TIMESTAMP),
					new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_SADYEAR", request.getSadYear());
			paramSource.put("P_CUSTOMSCODE", request.getCustomsCode());
			paramSource.put("P_SADASSESSMENTSERIAL", request.getSadAssessmentSerial());
			paramSource.put("P_SADASSESSMENTNUMBER", request.getSadAssessmentNumber());
			paramSource.put("P_VERSION", request.getVersion());
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
					isUpdated = 1;
					log.info("Successfully updated the payment details with request: " + request.toString()
							+ " as acknowledge");

				} else if (responsecode.equals("99")) {
					log.warn("db error occured with message: " + respValues.get("P_RESPONSEMSG"));
				}
			}
		} catch (Exception ex) {
			log.info("error while updating details with successful validation response because: " + ex.getMessage(),
					ex);
		}
		return isUpdated;
	}

	public int updatePaymentWithNCSResponse(TransactionResponse response) {
		int isUpdated = 0;
		log.info("trying to update payment details with ");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("UPDATEWITHNCSRESPONSE");
			call.declareParameters(new SqlParameter("P_SADYEAR", Types.INTEGER),
					new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
					new SqlParameter("P_VERSION", Types.VARCHAR),
					new SqlParameter("P_NCS_RESPCODE", Types.VARCHAR),
					new SqlParameter("P_NCS_RESPMSG", Types.VARCHAR),
					new SqlParameter("P_NCS_ERROR_CODE",Types.VARCHAR),
					new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR)

			);
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_SADYEAR", response.getSadAsmt().getSadYear());
			paramSource.put("P_CUSTOMSCODE", response.getCustomsCode());
			paramSource.put("P_SADASSESSMENTSERIAL", response.getSadAsmt().getSadAssessmentSerial());
			paramSource.put("P_SADASSESSMENTNUMBER", response.getSadAsmt().getSadAssessmentNumber());
			paramSource.put("P_VERSION", response.getVersion());
			paramSource.put("P_NCS_ERROR_CODE", response.getInfo().getMessage().getErrorCode());
			paramSource.put("P_NCS_RESPCODE", response.getTransactionStatus().value());
			paramSource.put("P_NCS_RESPMSG", response.getInfo().getMessage().getMessage());
			
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				String code = respValues.get("P_RESPONSECODE").toString();
				if (code.equals("00")) {
					isUpdated = 1;
					log.info("successfully updated payment with NCS response");
				} else {
					String errorMessage = respValues.get("P_RESPONSEMSG").toString();
					log.warn("db error occured with error message: " + errorMessage);
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to update payment with NCS response because: " + ex.getMessage(), ex);
		}
		return isUpdated;
	}

	public int saveTax(TaxEntity tax) {
		int isSaved = 0;
		log.info("trying to save tax entity");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("SAVETAX");
			call.declareParameters(new SqlParameter("P_TAXCODE", Types.VARCHAR),
					new SqlParameter("P_TAXAMOUNT", Types.VARCHAR), new SqlParameter("P_PAYMENTID", Types.NUMERIC),
					new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_TAXCODE", tax.getTaxCode());
			paramSource.put("P_TAXAMOUNT", tax.getTaxAmount());
			paramSource.put("P_PAYMENTID", tax.getPaymentDetailsId());
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				String rspCode = respValues.get("P_RESPONSECODE").toString();
				if (rspCode.equals("00")) {
					isSaved = 1;
					log.info("successfully saved tax entity");
				} else {
					log.warn("db error occured while trying to save tax entity with error message: "
							+ respValues.get("P_RESPONSEMSG").toString());
				}
			}
		} catch (Exception ex) {
			log.error("Error occured while trying to save tax entity because: " + ex.getMessage(), ex);
		}
		return isSaved;
	}





	public List<SweepPaymentDetails> findFCUBSDetails(String reference, String code) {
		List<SweepPaymentDetails> models = new ArrayList<>();
		SweepPaymentDetails model = null;
//		ResultSet rs = null;
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.withFunctionName("GETSWEEPPAYMENTDETAILS");
//			call.declareParameters(new SqlOutParameter("PAYMENTDETAILS", Types.REF_CURSOR));
			SqlParameterSource paramSource = new MapSqlParameterSource().addValue("paymentref", reference)
					.addValue("task_code", code);
			Map<String, Object> result = call.execute(paramSource);
			if (!result.isEmpty()) {
				String rspCode = result.get("return").toString();
				if (rspCode.equals("00")) {
					if (result.get("PAYMENTDETAILS") != null) {
						ArrayList<?> list = (ArrayList<?>) result.get("PAYMENTDETAILS");
						JSONArray rsArray = new JSONArray(list);
						for (int i = 0; i < rsArray.length(); i++) {
							JSONObject rs = rsArray.getJSONObject(i);
							if (rs != null && rs.toString().startsWith("{")) {
								model = new SweepPaymentDetails();
								model.setAc_branch(rs.getString("AC_BRANCH"));
								model.setAc_no(rs.getString("AC_NO"));
								model.setAc_ccy(rs.getString("AC_CCY"));
								model.setTrn_code(rs.getString("TRN_CODE"));
								model.setLcy_amount(rs.getBigDecimal("AMOUNT").toString());
								model.setCust_gl(rs.getString("CUST_GL"));
								model.setExternal_ref_no(rs.getString("EXTERNAL_REF_NO"));
								model.setDrcr_ind(rs.getString("DRCR_IND"));
								model.setFormmnumber(rs.optString("FORMMNUMBER"));
								model.setTask_code(code);

								models.add(model);
							}
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

	public String persistSweepInfo(SweepPersistAgent sweepAgent) {
		String responsecode = "99";
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.withFunctionName("PERSISTSWEEPDATA");
			SqlParameterSource paramSource = new MapSqlParameterSource().addValue("BATCH_ID", sweepAgent.getBatchid())
					.addValue("INITIATING_BRANCH", sweepAgent.getInitiatingbranch())
					.addValue("REQUEST_MODULE", sweepAgent.getRequestmodule())
					.addValue("MODULE_CREDENTIALS", sweepAgent.getModulecredentials())
					.addValue("CURRENCY", sweepAgent.getTransactioncurrency())
					.addValue("ACCOUNT", sweepAgent.getAcccountnumber())
					.addValue("BRANCH_CODE", sweepAgent.getBranchcode()).addValue("SRAMOUNT", sweepAgent.getAmount())
					.addValue("DEBITCREDITIND", sweepAgent.getDebitcreditindicator())
					.addValue("GLCASAIND", sweepAgent.getGlcasaindicator())
					.addValue("NEMONIC", sweepAgent.getMnemonic()).addValue("NARATION", sweepAgent.getNarration())
					.addValue("NCSREFERENCE", sweepAgent.getNcscustomreference())
					.addValue("PAYMENT_REFERENCE", sweepAgent.getPaymentreference())
					.addValue("TRANS_ID", sweepAgent.getTransactionid())
					.addValue("RESP_CODE", sweepAgent.getResponsecode())
					.addValue("RESP_MESG", sweepAgent.getResponsemessage())
//					.addValue("RESPONSE_TIME", sweepAgent.getResponsetime()).addValue("UIDSS", sweepAgent.getUids())
					.addValue("BATCHDETAILID", sweepAgent.getBatch_detail_id())
					.addValue("PAYMENTTYPEID", sweepAgent.getPayment_type_id())
					.addValue("SWEEPSTATUS", sweepAgent.getIssweeporreversal())
					.addValue("NCSSTATUS", sweepAgent.getNcstransstatus());
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				responsecode = respValues.get("return").toString();
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
		return responsecode;
	}
	
	public SweepPaymentResponse UpdatePayments(String paymentref, String vresp, String siflag, String fcubscount,String trn_resp) {
		SweepPaymentResponse response = new SweepPaymentResponse();
		String responsecode = "01";
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.withFunctionName("UPDATE_PAYMENTS");
			SqlParameterSource paramSource = new MapSqlParameterSource()
					.addValue("PAYMENT_REFERENCE", paymentref)
					.addValue("CODE", vresp)
					.addValue("SIFLAG", siflag)
					.addValue("FCUBSCOUNT", fcubscount)
					.addValue("TRN_RESP", trn_resp);			
			Map<String, Object> respValues = call.execute(paramSource);
			if (!respValues.isEmpty()) {
				responsecode = respValues.get("return").toString();
				if (responsecode.equals("00")) {
					log.info("Successfully updated payment tables!");
				} else if (responsecode.equals("01")) {
					log.warn("db error occured with message: " + responsecode);
				}
				response.setUpdate_response(responsecode);
			}
		} catch (Exception ex) {
			log.error("error occured while trying to set payment details as acknowledge becuase: " + ex.getMessage(),
					ex);
		}
		return response;
	}

}
