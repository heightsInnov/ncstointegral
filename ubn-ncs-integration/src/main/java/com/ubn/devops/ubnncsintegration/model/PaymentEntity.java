package com.ubn.devops.ubnncsintegration.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ubn.devops.ubnncsintegration.ncsschema.EPaymentConfirmation;
import com.ubn.devops.ubnncsintegration.ncsschema.Payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "NCS_PAYMENT")
public class PaymentEntity {

	public PaymentEntity(EPaymentConfirmation paymentConfirmation) {
		if (paymentConfirmation != null) {
			this.customsCode = paymentConfirmation.getCustomsCode();
			this.declarantCode = paymentConfirmation.getDeclarantCode();
			this.bankCode = paymentConfirmation.getBankCode();
			if (paymentConfirmation.getSadAsmt() != null) {
				this.sadAssessmentNumber = paymentConfirmation.getSadAsmt().getSadAssessmentNumber();
				this.sadAssessmentSerial = paymentConfirmation.getSadAsmt().getSadAssessmentSerial();
				this.sadYear = paymentConfirmation.getSadAsmt().getSadYear();
			}
			if (!paymentConfirmation.getPayment().isEmpty()) {
				Payment payment = paymentConfirmation.getPayment().get(0);
				if (payment != null) {
					this.amount = payment.getAmount();
					this.reference = payment.getReference();
					this.totalPaid = payment.getAmount();
				}
			}

		}
	}

	@Id
	@GeneratedValue(generator = "payment_gen", strategy = GenerationType.IDENTITY)
	@SequenceGenerator(allocationSize = 1, initialValue = 1, name = "payment_gen", sequenceName = "payment_gen")
	private Long id;

	private String customsCode;
	private String declarantCode;
	private String bankCode;
	private String sadAssessmentSerial;
	private String sadAssessmentNumber;
	private int sadYear;
	private String meansOfPayment;
	private String reference;
	private double amount;
	private double totalPaid;
	private String transactionstatus;
	private String responseMessage;

}
