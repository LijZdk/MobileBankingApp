package com.smithinc.mobile_banking_supporting_classes;

public class User {
	
	private String mUsername, mPassword, mPin;
	
	public User (String username, String password, String pin) {
		mUsername = username;
		mPassword = password;
		mPin = pin;
	}
	
	public String getUsername () {
		return mUsername;
	}
	
	public String getPassword () {
		return mPassword;
	}
	
	public String getPin () {
		return mPin;
	}
	
}
