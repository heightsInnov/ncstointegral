package com.ubn.devops.ubnncsintegration.model;

public class AccountEnquiry {

	private String accountNumber;
	private String accountName;
	private String accountBranchCode;
	private String customerNumber;
	private String accountCurrency;
	private String accountType;
	private Double availableBalance;
	private String customerEmail;
	private String customerPhoneNumber;
	
	public AccountEnquiry(String accountNumber, String accountName, String accountBranchCode, String customerNumber,
			String accountCurrency, String accountType, Double availableBalance, String customerEmail,
			String customerPhoneNumber) {
		super();
		this.accountNumber = accountNumber;
		this.accountName = accountName;
		this.accountBranchCode = accountBranchCode;
		this.customerNumber = customerNumber;
		this.accountCurrency = accountCurrency;
		this.accountType = accountType;
		this.availableBalance = availableBalance;
		this.customerEmail = customerEmail;
		this.customerPhoneNumber = customerPhoneNumber;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountBranchCode() {
		return accountBranchCode;
	}
	public void setAccountBranchCode(String accountBranchCode) {
		this.accountBranchCode = accountBranchCode;
	}
	public String getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getAccountCurrency() {
		return accountCurrency;
	}
	public void setAccountCurrency(String accountCurrency) {
		this.accountCurrency = accountCurrency;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public Double getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}
	public void setCustomerPhoneNumber(String customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}
}
