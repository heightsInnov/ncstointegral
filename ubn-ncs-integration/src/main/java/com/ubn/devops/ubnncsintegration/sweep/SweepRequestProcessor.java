package com.ubn.devops.ubnncsintegration.sweep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ubn.devops.ubnncsintegration.config.FilePathsConfig;
import com.ubn.devops.ubnncsintegration.model.AccountEnquiry;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPersistAgent;
import com.ubn.devops.ubnncsintegration.repository.PaymentDetailsRepository;
import com.ubn.devops.ubnncsintegration.response.SweepReverseResponse;
import com.ubn.devops.ubnncsintegration.serviceimpl.TokenGenerator;

public class SweepRequestProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FilePathsConfig config;
	
	private String ubntransitgl = config.getUbntransitgl();

	private String ubntransitgl_name = config.getUbntransitgl_name();

	private String tsaaccountno = config.getTsaaccountno();

	private String tsaaccount_name = config.getTsaaccount_name();

	private String tsaaccount_branch = config.getTsaaccount_branch();

	private String narations = config.getNarations();
	
	private String init_branch = config.getInit_branch();

	private String token_url = config.getToken_url();

	private String posting_url = config.getPosting_url();

	private String accteqry_url = config.getAccteqry_url();

	@Autowired
	TokenGenerator tokenize;

	@Autowired
	SweepingAndPosting restService;

	@Autowired
	PaymentDetailsRepository paymentDetailsRepository;

	public String DoSweepPostingProcess(String fcubs_reference, String task_code) {
		List<SweepPersistAgent> persistData = new ArrayList<>();
		SweepReverseResponse response = new SweepReverseResponse();
		String resp="01";
		SweepReverseResponse accEnq;
		String naration = "";

		List<SweepPaymentDetails> requests = paymentDetailsRepository.findFCUBSDetails(fcubs_reference, task_code);

		String narate = narations.replace("{Type}", "" != null ? "" : "").replace("{TargetIdentifier}",
				requests.get(0).getFormmnumber());

		try {
			if (requests != null) {
				JSONArray batchItems = new JSONArray();
				JSONObject creditItem;
				AccountEnquiry account;
				JSONObject debititem;
				SweepPersistAgent debitSweep;
				SweepPersistAgent creditSweep;
				SweepPersistAgent saveSweep;
				if (task_code.equals("S")) {
					for (SweepPaymentDetails request : requests) {
						naration = narate.replace("{TypeDescription}", "recharge ");
						accEnq = AccountEnquiryCheck(tsaaccountno);
						if (accEnq != null & accEnq.getCode().equals("00")) {
							account = (AccountEnquiry) accEnq.getData();

							String transid = String.valueOf(System.nanoTime());

							// Credit leg of Sweep
							creditItem = new JSONObject();
							creditSweep = new SweepPersistAgent();

							creditItem.put("transactionId", transid);
							creditItem.put("accountNumber", tsaaccountno);
							creditItem.put("accountType", tsaaccountno.length() == 10 ? "CASA" : "GL");
							creditItem.put("accountName", account.getAccountName());
							creditItem.put("accountBranchCode", account.getAccountBranchCode());
							creditItem.put("accountBankCode", "032");
							creditItem.put("narration", naration);
							creditItem.put("instrumentNumber", "");
							creditItem.put("amount", request.getLcy_amount());
							creditItem.put("valueDate", new Date());
							creditItem.put("crDrFlag", "C");
							creditItem.put("feeOrCharges", false);

							creditSweep.setCreditacccountnumber(tsaaccountno);
							creditSweep.setBranchcode(account.getAccountBranchCode());
							creditSweep.setAmount(request.getLcy_amount());
							creditSweep.setDebitcreditindicator("C");
							creditSweep.setNarration(naration);
							creditSweep.setNcscustomreference(request.getFormmnumber());
							creditSweep.setPaymentreference(
									requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
							creditSweep.setTransactionid(transid);
							creditSweep.setUids(String.valueOf(UUID.randomUUID()));
							creditSweep.setPayment_type_id(
									requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
							creditSweep.setIssweeporreversal("S");
							creditSweep.setNcstransstatus("S");

							batchItems.put(creditItem);
							persistData.add(creditSweep);

							// Debit leg of Sweep
							debititem = new JSONObject();
							debitSweep = new SweepPersistAgent();

							debititem.put("transactionId", transid);
							debititem.put("accountNumber", ubntransitgl);
							debititem.put("accountType", "GL");
							debititem.put("accountName", ubntransitgl_name);
							debititem.put("accountBranchCode", init_branch);
							debititem.put("accountBankCode", "032");
							debititem.put("narration", naration);
							debititem.put("instrumentNumber", "");
							debititem.put("amount", request.getLcy_amount());
							debititem.put("valueDate", new Date());
							debititem.put("crDrFlag", "D");
							debititem.put("feeOrCharges", false);

							debitSweep.setDebitacccountnumber(ubntransitgl);
							debitSweep.setBranchcode(init_branch);
							debitSweep.setAmount(request.getLcy_amount());
							debitSweep.setDebitcreditindicator("D");
							debitSweep.setNarration(naration);
							debitSweep.setNcscustomreference(request.getFormmnumber());
							debitSweep.setPaymentreference(
									requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
							debitSweep.setTransactionid(transid);
							debitSweep.setUids(String.valueOf(UUID.randomUUID()));
							debitSweep.setPayment_type_id(
									requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
							debitSweep.setIssweeporreversal("S");
							debitSweep.setNcstransstatus("S");

							batchItems.put(debititem);
							persistData.add(debitSweep);
						}
					}
				} else if (task_code.equals("R")) {
					saveSweep = new SweepPersistAgent();
					JSONObject item = new JSONObject();
					for (SweepPaymentDetails request : requests) {
						naration = narate.replace("{TypeDescription}", "bills payment ");
						accEnq = AccountEnquiryCheck(request.getAc_no());
						if (accEnq != null & accEnq.getCode().equals("00")) {
							account = (AccountEnquiry) accEnq.getData();

							String transid = String.valueOf(System.nanoTime());
							item.put("transactionId", transid);
							item.put("accountNumber", request.getAc_no());
							item.put("accountType", request.getAc_no().length() == 10 ? "CASA" : "GL");
							item.put("accountName", account.getAccountName());
							item.put("accountBranchCode", account.getAccountBranchCode());
							item.put("accountBankCode", "032");
							item.put("narration", naration);
							item.put("instrumentNumber", "");
							item.put("amount", request.getLcy_amount());
							item.put("valueDate", new Date());
							if (request.getDrcr_ind().equals("D")) {
								item.put("crDrFlag", "C");
								saveSweep.setCreditacccountnumber(request.getAc_no());
								saveSweep.setDebitcreditindicator("C");
							} else if (request.getDrcr_ind().equals("C")) {
								item.put("crDrFlag", "D");
								saveSweep.setDebitacccountnumber(request.getAc_no());
								saveSweep.setDebitcreditindicator("D");
							}
							item.put("feeOrCharges", false);

							batchItems.put(item);

							saveSweep.setBranchcode(account.getAccountBranchCode());
							saveSweep.setAmount(request.getLcy_amount());
							saveSweep.setGlcasaindicator(request.getAc_no().length() == 10 ? "CASA" : "GL");
							saveSweep.setNarration(naration);
							saveSweep.setNcscustomreference(request.getFormmnumber());
							saveSweep.setPaymentreference(
									requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
							saveSweep.setTransactionid(transid);
							saveSweep.setUids(String.valueOf(UUID.randomUUID()));
							saveSweep.setPayment_type_id(
									requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
							saveSweep.setIssweeporreversal("R");
							saveSweep.setNcstransstatus("F");

							persistData.add(saveSweep);
						}
					}
				}
				JSONObject postingObject = new JSONObject();

				postingObject.put("currency", "NGN");
				postingObject.put("initBranchCode", init_branch);
				postingObject.put("paymentReference",
						requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
				postingObject.put("transactionDate", new Date());
				postingObject.put("paymentTypeCode", "FT");
				postingObject.put("externalSystemReference", "");
				postingObject.put("batchItems", batchItems);

				response = restService.PostorSweep(accteqry_url + tokenize.getToken(), postingObject);
			}
			for (SweepPersistAgent saveSweep : persistData) {
				saveSweep.setResponsecode(response.getCode());
				saveSweep.setResponsemessage(response.getMessage());
				saveSweep.setBatch_detail_id(response.getBody() != null ? response.getBody().split("|")[0] : "");

				saveSweep.setBatchid(response.getBody() != null ? response.getBody().split("|")[1] : "");
				saveSweep.setInitiatingbranch(init_branch);
				saveSweep.setTransactioncurrency("NGN");

				saveSweep.setRequestmodule("NCS_TRANS");
				saveSweep.setModulecredentials("");
				saveSweep.setMnemonic("");
			}
			SaveSweepTransaction(persistData);

		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		return resp;
	}

	private void SaveSweepTransaction(List<SweepPersistAgent> sweepData) {
		for(SweepPersistAgent sweepAgent : sweepData) {
			paymentDetailsRepository.persistSweepInfo(sweepAgent);
		}
	}

	public SweepReverseResponse AccountEnquiryCheck(String accountNo) throws IOException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("accountNumber", accountNo);
		SweepReverseResponse resp = restService.PostorSweep(accteqry_url + tokenize.getToken(), jsonObject);
		return resp;
	}
}
