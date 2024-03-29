package com.ubn.devops.ubnncsintegration.sweep;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ubn.devops.ubnncsintegration.model.AccountEnquiry;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentDetails;
import com.ubn.devops.ubnncsintegration.model.SweepPaymentResponse;
import com.ubn.devops.ubnncsintegration.model.SweepPersistAgent;
import com.ubn.devops.ubnncsintegration.repository.PaymentDetailsRepository;
import com.ubn.devops.ubnncsintegration.response.SweepReverseResponse;
import com.ubn.devops.ubnncsintegration.serviceimpl.GenerateToken;
import com.ubn.devops.ubnncsintegration.utility.PropsReader;

@Component
public class SweepRequestProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

//	private String ubntransitgl = PropsReader.getValue("ncs.sweep.gl.account");

//	private String ubntransitgl_name = PropsReader.getValue("ncs.sweep.gl.account.name");

	private String tsaaccountno = PropsReader.getValue("ncs.sweep.tsa.account");

//	private String tsaaccountno_name = PropsReader.getValue("ncs.sweep.tsa.account.name");

	private String tsaaccountno_branchcode = PropsReader.getValue("ncs.sweep.tsa.account.branchcode");

	private String narations = PropsReader.getValue("ncs.sweep.trans.notification");

	private String init_branch = PropsReader.getValue("ncs.sweep.gl.branch");

	private String token_url = PropsReader.getValue("ncs.token.url");

	private String posting_url = PropsReader.getValue("ncs.posting.url");

	private String accteqry_url = PropsReader.getValue("ncs.accteqry.url");

	@Autowired
	GenerateToken tokenize;

	@Autowired
	SweepingAndPosting restService;

	@Autowired
	PaymentDetailsRepository paymentDetailsRepository;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public SweepPaymentResponse DoSweepPostingProcess(String fcubs_reference, String status_code) {
		SweepPaymentResponse resp = new SweepPaymentResponse();
		String task_code = status_code.toUpperCase().equals("OK") ? "S"
				: status_code.toUpperCase().equals("ERROR") ? "R" : "R";
		List<SweepPersistAgent> persistData = new ArrayList<>();
		SweepReverseResponse response = new SweepReverseResponse();
		SweepReverseResponse accEnq;
		String naration = narations;

		List<SweepPaymentDetails> requests = paymentDetailsRepository.findFCUBSDetails(fcubs_reference, task_code);

		try {
			if (requests != null) {
				JSONArray batchItems = new JSONArray();
				JSONObject creditItem;
				AccountEnquiry account = null;
				JSONObject debititem;
				SweepPersistAgent debitSweep;
				SweepPersistAgent creditSweep;
				SweepPersistAgent saveSweep;

				if (task_code.equals("S")) {
					String uuid = String.valueOf(UUID.randomUUID());
					for (SweepPaymentDetails request : requests) {
						naration = naration.replace("{action}", "sweep to");
						if (tsaaccountno.length() == 10) {
							accEnq = AccountEnquiryCheck(tsaaccountno);
							if (accEnq != null && accEnq.getCode().equals("00"))
								account = (AccountEnquiry) accEnq.getData();
						}

						String transid = String.valueOf(System.nanoTime());
						String bcode = account != null ? account.getAccountBranchCode() : tsaaccountno_branchcode;
						// Credit leg of Sweep
						creditItem = new JSONObject();
						creditSweep = new SweepPersistAgent();

						creditItem.put("transactionId", transid);
						creditItem.put("accountNumber", tsaaccountno);
						creditItem.put("accountType", tsaaccountno.length() == 10 ? "CASA" : "GL");
//							creditItem.put("accountName", account!=null ?account.getAccountName():tsaaccountno_name);
						creditItem.put("accountBranchCode", bcode);
						creditItem.put("accountBankCode", "032");
						creditItem.put("narration", naration);
						creditItem.put("instrumentNumber", "");
						creditItem.put("amount", request.getLcy_amount());
						creditItem.put("valueDate", sdf.format(new Date()));
						creditItem.put("crDrFlag", "C");
						creditItem.put("feeOrCharges", false);

						creditSweep.setAcccountnumber(tsaaccountno);
						creditSweep.setBranchcode(bcode);
						creditSweep.setAmount(request.getLcy_amount());
						creditSweep.setDebitcreditindicator("C");
						creditSweep.setNarration(naration);
						creditSweep.setNcscustomreference(request.getFormmnumber());
						creditSweep.setPaymentreference(
								requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
						creditSweep.setTransactionid(transid);
						creditSweep.setUids(uuid);
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
						debititem.put("accountNumber", request.getAc_no());
						debititem.put("accountType", "GL");
//							debititem.put("accountName", ubntransitgl_name);
						debititem.put("accountBranchCode", request.getAc_branch());
						debititem.put("accountBankCode", "032");
						debititem.put("narration", naration);
						debititem.put("instrumentNumber", "");
						debititem.put("amount", request.getLcy_amount());
						debititem.put("valueDate", sdf.format(new Date()));
						debititem.put("crDrFlag", "D");
						debititem.put("feeOrCharges", false);

						debitSweep.setAcccountnumber(request.getAc_no());
						debitSweep.setBranchcode(request.getAc_branch());
						debitSweep.setAmount(request.getLcy_amount());
						debitSweep.setDebitcreditindicator("D");
						debitSweep.setNarration(naration);
						debitSweep.setNcscustomreference(request.getFormmnumber());
						debitSweep.setPaymentreference(
								requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
						debitSweep.setTransactionid(transid);
						debitSweep.setUids(uuid);
						debitSweep.setPayment_type_id(
								requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
						debitSweep.setIssweeporreversal("S");
						debitSweep.setNcstransstatus("S");

						batchItems.put(debititem);
						persistData.add(debitSweep);
					}
				} else if (task_code.equals("R")) {
					JSONObject item;

					for (SweepPaymentDetails request : requests) {
						naration = naration.replace("{action}", "reversal from");

						if (request.getAc_no().length() == 10) {
							accEnq = AccountEnquiryCheck(request.getAc_no());
							if (accEnq != null & accEnq.getCode().equals("00"))
								account = (AccountEnquiry) accEnq.getData();
						}
						item = new JSONObject();
						saveSweep = new SweepPersistAgent();

						String transid = String.valueOf(System.nanoTime());
						item.put("transactionId", transid);
						item.put("accountNumber", request.getAc_no());
						item.put("accountType", request.getAc_no().length() == 10 ? "CASA" : "GL");
//							item.put("accountName", account.getAccountName());
						item.put("accountBranchCode", request.getAc_branch());
						item.put("accountBankCode", "032");
						item.put("narration", naration);
						item.put("instrumentNumber", "");
						item.put("amount", request.getLcy_amount());
						item.put("valueDate", sdf.format(new Date()));
						if (request.getDrcr_ind().equals("D")) {
							item.put("crDrFlag", "C");
							saveSweep.setAcccountnumber(request.getAc_no());
							saveSweep.setDebitcreditindicator("C");
						} else if (request.getDrcr_ind().equals("C")) {
							item.put("crDrFlag", "D");
							saveSweep.setAcccountnumber(request.getAc_no());
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
				JSONObject postingObject = new JSONObject();

				postingObject.put("currency", "NGN");
				postingObject.put("initBranchCode", init_branch);
				postingObject.put("paymentReference",
						requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code());
				postingObject.put("transactionDate", sdf.format(new Date()));
				postingObject.put("paymentTypeCode", "FT");
				postingObject.put("externalSystemReference", "");
				postingObject.put("batchItems", batchItems);

				response = restService.PostorSweep(posting_url + tokenize.getToken(token_url), postingObject);
			}
			for (SweepPersistAgent saveSweep : persistData) {
				saveSweep.setResponsecode(response.getCode());
				saveSweep.setResponsemessage(response.getMessage());
				saveSweep.setBatch_detail_id(!response.getBody().equals("empty") && response.getBody().contains("|")
						? response.getBody().split("|")[0]
						: "empty");

				System.out.println(response.getBody().toString());
				saveSweep.setBatchid(!response.getBody().equals("empty") && response.getBody().contains("|")
						? response.getBody().split("|")[1]
						: "empty");
				saveSweep.setInitiatingbranch(init_branch);
				saveSweep.setTransactioncurrency("NGN");

				saveSweep.setRequestmodule("NCS_TRANS");
				saveSweep.setModulecredentials("");
				saveSweep.setMnemonic("");
			}
			Map<String, String> in_resp = SaveSweepTransaction(persistData);
			resp = paymentDetailsRepository.UpdatePayments(
					requests.get(0).getExternal_ref_no() + requests.get(0).getTask_code(), task_code,
					in_resp.get("count"), in_resp.get("list"), response.getCode());
			resp.setMiserve_response(response.getCode().equals("00") ? "00" : "99");
		} catch (Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		return resp;
	}

	private Map<String, String> SaveSweepTransaction(List<SweepPersistAgent> sweepData) {
		Map<String, String> response = new HashMap<String, String>();
		String[] respArray = new String[sweepData.size()];
		int i = 0;
		try {
			for (SweepPersistAgent sweepAgent : sweepData) {
				respArray[i] = paymentDetailsRepository.persistSweepInfo(sweepAgent);
				i++;
			}
			int count = 0;
			for (int j = 0; j < respArray.length; j++) {
				if (respArray[j].equals("00"))
					count++;
			}
			response.put("list", String.valueOf(sweepData.size()));
			response.put("count", String.valueOf(count));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public SweepReverseResponse AccountEnquiryCheck(String accountNo) throws IOException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("accountNumber", accountNo);
		SweepReverseResponse resp = restService.PostorSweep(accteqry_url + tokenize.getToken(token_url), jsonObject);
		return resp;
	}
}
