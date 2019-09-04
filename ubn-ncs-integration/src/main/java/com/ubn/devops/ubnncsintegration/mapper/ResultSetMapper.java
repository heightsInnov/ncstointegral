package com.ubn.devops.ubnncsintegration.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.ubn.devops.ubnncsintegration.model.PaymentModel;

public class ResultSetMapper implements RowMapper<PaymentModel>{

	
	public static List<PaymentModel> mapRow(ResultSet rs) throws SQLException {
		List<PaymentModel> payments = new ArrayList<>();
		while(rs.next()) {
			PaymentModel payment = new PaymentModel();
			payment.setAmount((rs.getBigDecimal("AMOUNT")).doubleValue());
			payment.setBankBranchCode(rs.getString("BANK_BRANCH_CODE"));
			payment.setBankCode(rs.getString("BANK_CODE"));
			payment.setCompanyCode(rs.getString("COMPANY_CODE"));
			payment.setCompanyName(rs.getString("COMPANY_NAME"));
			payment.setCreatedDate(rs.getDate("CREATED_DATE"));
			payment.setCustomsCode(rs.getString("CUSTOMS_CODE"));
			payment.setDeclarantCode(rs.getString("DECLARANT_CODE"));
			payment.setDeclarantName(rs.getString("DECLARANT_NAME"));
			payment.setFormMNumber(rs.getString("FORMMNUMBER"));
			payment.setId((rs.getBigDecimal("ID")).longValue());
			payment.setMeansOfPayment(rs.getString("MEANS_OF_PAYMENT"));
			payment.setPaid(rs.getBoolean("IS_PAID"));
			payment.setReference(rs.getString("REFERENCE"));
			payment.setResponseMessage(rs.getString("RESPONSE_MESSAGE"));
			payment.setSadAssessmentDate(rs.getDate("SAD_ASSESSMENT_DATE").toString());
			payment.setSadAssessmentNumber(rs.getString("SAD_ASSESSMENT_NUMBER"));
			payment.setSadAssessmentSerial(rs.getString("SAD_ASSESSMENT_SERIAL"));
			payment.setSadYear(rs.getInt("SAD_YEAR"));
			payment.setTotalAmountToBePaid((rs.getBigDecimal("TOTAL_AMOUNT_TO_BE_PAID")).doubleValue());
			payment.setTransactionstatus(rs.getString("TRANSACTIONSTATUS"));
			payment.setCustomerEmail(rs.getString("CUSTOMER_EMAIL"));
			payment.setFcubsPostingRef(rs.getString("FCUBS_POSTING_REF"));
			payment.setHashValue(rs.getString("HASH_VALUE"));
			payment.setIsPaymentReversed(rs.getString("IS_PAYMENT_REVERSED"));
			payment.setIsSweepedToTsa(rs.getString("IS_SWEEPED_TO_TSA"));
			payment.setPaymentChannelCode(rs.getString("PAYMENT_CHANNEL_CODE"));
			payment.setReversalDate(rs.getDate("REVERSAL_DATE"));
			payment.setReversalFcubsRef(rs.getString("REVERSAL_FCUBS_REF"));
			payment.setSweepDate(rs.getDate("SWEEP_DATE"));
			payment.setSweepFcubsPostRef(rs.getString("SWEEP_FCUBS_POST_REF"));
			payments.add(payment);
		}
		return payments;
	}

	@Override
	public PaymentModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		PaymentModel payment = new PaymentModel();
		payment.setAmount((rs.getBigDecimal("AMOUNT")).doubleValue());
		payment.setBankBranchCode(rs.getString("BANK_BRANCH_CODE"));
		payment.setBankCode(rs.getString("BANK_CODE"));
		payment.setCompanyCode(rs.getString("COMPANY_CODE"));
		payment.setCompanyName(rs.getString("COMPANY_NAME"));
		payment.setCreatedDate(rs.getDate("CREATED_DATE"));
		payment.setCustomsCode(rs.getString("CUSTOMS_CODE"));
		payment.setDeclarantCode(rs.getString("DECLARANT_CODE"));
		payment.setDeclarantName(rs.getString("DECLARANT_NAME"));
		payment.setFormMNumber(rs.getString("FORMMNUMBER"));
		payment.setId((rs.getBigDecimal("ID")).longValue());
		payment.setMeansOfPayment(rs.getString("MEANS_OF_PAYMENT"));
		payment.setPaid(rs.getBoolean("IS_PAID"));
		payment.setReference(rs.getString("REFERENCE"));
		payment.setResponseMessage(rs.getString("RESPONSE_MESSAGE"));
		payment.setSadAssessmentDate(rs.getDate("SAD_ASSESSMENT_DATE").toString());
		payment.setSadAssessmentNumber(rs.getString("SAD_ASSESSMENT_NUMBER"));
		payment.setSadAssessmentSerial(rs.getString("SAD_ASSESSMENT_SERIAL"));
		payment.setSadYear(rs.getInt("SAD_YEAR"));
		payment.setTotalAmountToBePaid((rs.getBigDecimal("TOTAL_AMOUNT_TO_BE_PAID")).doubleValue());
		payment.setTransactionstatus(rs.getString("TRANSACTIONSTATUS"));
		return payment;
	}

}
