package com.smithinc.mobile_banking;

public class Account {

	public String accountNumber;
	public String accountName;
	public double accountBalance;
	public boolean pendingTransfer;
	
	public Account (String accountNumber, String accountName, double accountBalance, boolean pendingTransfer)
	{
		this.accountNumber = accountNumber;
		this.accountName = accountName;
		this.accountBalance = accountBalance;
		this.pendingTransfer = pendingTransfer;
	}
	
}
