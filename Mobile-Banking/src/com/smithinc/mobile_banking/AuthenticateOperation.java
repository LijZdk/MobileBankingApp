package com.smithinc.mobile_banking;

public class AuthenticateOperation
{
	private enum answer
	{
		YES, NO
	}

	String kEndpoint = "/user/authenticate";
	String kHTTPMethod = "POST";
	double kOperationTimeout = 30.0;

	private String username;
	private String password;
	private String passphrase;
	private boolean registerDevice;

	private answer isExecuting = answer.NO;
	private answer isFinished = answer.NO;

	static answer operationInProcess = answer.NO;

	private String getUsername()
	{
		return username;
	}

	private void setUsername(String username)
	{
		this.username = username;
	}

	private String getPassword()
	{
		return password;
	}

	private void setPassword(String password)
	{
		this.password = password;
	}

	private String getPassphrase()
	{
		return passphrase;
	}

	private void setPassphrase(String passphrase)
	{
		this.passphrase = passphrase;
	}

	private boolean isRegisterDevice()
	{
		return registerDevice;
	}

	private void setRegisterDevice(boolean registerDevice)
	{
		this.registerDevice = registerDevice;
	}
}
