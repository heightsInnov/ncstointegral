package com.ubn.devops.ubnncsintegration.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ubn.devops.ubnncsintegration.mapper.CustomMapper;
import com.ubn.devops.ubnncsintegration.model.PaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentDetails;
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
				call.declareParameters(new SqlParameter("P_SADYEAR", Types.INTEGER),
						new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
						new SqlParameter("P_DECLARANTCODE", Types.VARCHAR),
						new SqlParameter("P_DECLARANTNAME", Types.VARCHAR),
						new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
						new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
						new SqlParameter("P_SADASSESSMENTDATE", Types.DATE),
						new SqlParameter("P_COMPANYCODE", Types.VARCHAR),
						new SqlParameter("P_COMPANYNAME", Types.VARCHAR), 
						new SqlParameter("BANKCODE", Types.VARCHAR),
						new SqlParameter("BANKBRANCHCODE", Types.VARCHAR),
						new SqlParameter("P_FORMMNUMBER", Types.VARCHAR),
						new SqlParameter("P_TOTALAMOUNTTOBEPAID", Types.DECIMAL),
						new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
						new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR),
						new SqlOutParameter("P_ID", Types.DECIMAL),
						new SqlParameter("P_VERSION",Types.VARCHAR));
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
				Map<String, Object> respValues = call.execute(paramSource);
				if (!respValues.isEmpty()) {
					String responsecode = respValues.get("P_RESPONSECODE").toString();
					if (responsecode.equals("00")) {
						log.info("Successfully saved the payment details with declarant code: "
								+ assessmentNotice.getDeclarantCode());
						payment.setId(((BigDecimal) respValues.get("P_ID")).longValue());
						for(TaxEntity tax:payment.getTaxes()) {
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
	
	public int updatePaymentWithNCSResponse(TransactionResponse response) {
		int isUpdated = 0;
		log.info("trying to update payment details with ");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("UPDATESUCCESSFULVALIDATION");
			call.declareParameters(new SqlParameter("P_SADYEAR", Types.INTEGER),
					new SqlParameter("P_CUSTOMSCODE", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTSERIAL", Types.VARCHAR),
					new SqlParameter("P_SADASSESSMENTNUMBER", Types.VARCHAR),
					new SqlParameter("P_VERSION", Types.VARCHAR),
					new SqlParameter("P_NCS_RESPCODE", Types.VARCHAR),
					new SqlParameter("P_NCS_RESPMSG", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSEMSG", Types.DECIMAL)
					
					);
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_SADYEAR", response.getSadAsmt().getSadYear());
			paramSource.put("P_CUSTOMSCODE", response.getCustomsCode());
			paramSource.put("P_SADASSESSMENTSERIAL", response.getSadAsmt().getSadAssessmentSerial());
			paramSource.put("P_SADASSESSMENTNUMBER", response.getSadAsmt().getSadAssessmentNumber());
			paramSource.put("P_VERSION", response.getSadAsmt().getSadYear());
			paramSource.put("P_NCS_RESPCODE", response.getTransactionStatus().value());
			paramSource.put("P_NCS_RESPMSG", StringUtils.collectionToDelimitedString(response.getInfo().getMessage(),","));
			Map<String, Object> respValues = call.execute(paramSource);
			if(!respValues.isEmpty()) {
				String code = respValues.get("P_RESPONSECODE").toString();
				if(code.equals("00")) {
					isUpdated=1;
					log.info("successfully updated payment with NCS response");
				}else {
					String errorMessage = respValues.get("P_RESPONSEMSG").toString();
					log.warn("db error occured with error message: "+errorMessage);
				}
			}				
		}catch(Exception ex) {
			log.error("Error occured while trying to update payment with NCS response because: "+ex.getMessage(),ex);
		}
		return isUpdated;
	}
	
	public int saveTax(TaxEntity tax) {
		int isSaved=0;
		log.info("trying to save tax entity");
		try {
			SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate);
			call.setSchemaName(SCHEMANAME);
			call.setCatalogName(PACKAGENAME);
			call.setProcedureName("SAVETAX");
			call.declareParameters(new SqlParameter("P_TAXCODE", Types.VARCHAR),
					new SqlParameter("P_TAXAMOUNT", Types.VARCHAR),
					new SqlParameter("P_PAYMENTID", Types.NUMERIC),
					new SqlOutParameter("P_RESPONSECODE", Types.VARCHAR),
					new SqlOutParameter("P_RESPONSEMSG", Types.VARCHAR));
			Map<String, Object> paramSource = new HashMap<>();
			paramSource.put("P_TAXCODE", tax.getTaxCode());
			paramSource.put("P_TAXAMOUNT", tax.getTaxAmount());
			paramSource.put("P_PAYMENTID", tax.getPaymentDetailsId());
			Map<String, Object> respValues = call.execute(paramSource);
			if(!respValues.isEmpty()) {
				String rspCode = respValues.get("P_RESPONSECODE").toString();
				if(rspCode.equals("00")) {
					isSaved = 1;
					log.info("successfully saved tax entity");
				}else {
					log.warn("db error occured while trying to save tax entity with error message: "+respValues.get("P_RESPONSEMSG").toString());
				}
			}
		}catch(Exception ex) {
			log.error("Error occured while trying to save tax entity because: "+ex.getMessage(),ex);
		}
		return isSaved;
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
