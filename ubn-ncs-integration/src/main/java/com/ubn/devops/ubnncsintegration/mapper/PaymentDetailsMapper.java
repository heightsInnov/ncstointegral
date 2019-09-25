package com.ubn.devops.ubnncsintegration.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ubn.devops.ubnncsintegration.model.PaymentDetails;

public class PaymentDetailsMapper implements RowMapper<PaymentDetails>{
	
	@Override
	public PaymentDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		PaymentDetails payment = new PaymentDetails();
		payment.setAmount(rs.getBigDecimal("AMOUNT"));
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
		payment.setPaymentStatus(rs.getString("PAYMENT_STATUS"));
		payment.setResponseCode(rs.getString("RESPONSE_CODE"));
		payment.setResponseMessage(rs.getString("RESPONSE_MESSAGE"));
		payment.setSadAssessmentDate(rs.getDate("SAD_ASSESSMENT_DATE").toString());
		payment.setSadAssessmentNumber(rs.getString("SAD_ASSESSMENT_NUMBER"));
		payment.setSadAssessmentSerial(rs.getString("SAD_ASSESSMENT_SERIAL"));
		payment.setSadYear(rs.getInt("SAD_YEAR"));
		payment.setTotalAmountToBePaid(rs.getBigDecimal("TOTAL_AMOUNT_TO_BE_PAID"));
		payment.setTransactionstatus(rs.getString("TRANSACTIONSTATUS"));
		payment.setCustomerEmail(rs.getString("CUSTOMER_EMAIL"));
		payment.setFcubsPostingRef(rs.getString("FCUBS_POSTING_REF"));
		payment.setPaymentChannelCode(rs.getString("PAYMENT_CHANNEL_CODE"));
		payment.setPostingDate(rs.getDate("PAYMENT_DATE"));
		payment.setIsSweptOrReversed(rs.getString("IS_SWEPT_REVERSED"));
		payment.setSweepOrReversedDate(rs.getDate("REVERSED_SWEEP_DATE"));
		payment.setSweeporReversedRef(rs.getString("REVERSED_SWEEP_POST_REF"));
		return payment;
	}

}
