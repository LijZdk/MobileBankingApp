package com.smithinc.mobile_banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smithinc.mobile_banking.DashboardActivity.AccountsTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TransferFundsActivity extends Activity
{

	EditText mTransferTo;
	EditText mTransferFrom;
	EditText mAmount;
	Button mTransferButton;

	String mToAccount;
	String mFromAccount;
	String mAmountToTransfer;
	String mTransferDate;
	double mAmountToTransferD;

	private TransferTask mTransferTask;

	private static final String[] IP_ADDRESSES =
	{
	/* "ec2-54-200-161-9.us-west-2.compute.amazonaws.com/webservices/" */
	"129.252.226.44:8888",
	/*
	 * "192.168.1.76:8080" , "192.168.1.106:80", "10.251.4.220"
	 */
	};

	private static String HMAC_KEY = "065a62448fb75fce3764bcbe68f9908d";
	
	private static String AES_KEY = "b36013521d0f5dbea0e4ac1fd7af804a";

	private static String HASH_ALGORITHM = "HmacSHA256";
	
	private static String IV;
	
	private static String HASHED_MAC;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_transfer_funds);

		mTransferTo = (EditText) findViewById(R.id.toTextBox);
		mTransferFrom = (EditText) findViewById(R.id.fromTextBox);
		mAmount = (EditText) findViewById(R.id.amountTextBox);

		mTransferButton = (Button) findViewById(R.id.transferButton);

		findViewById(R.id.transferButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{

						transfer();

					}
				});

	}

	public void transfer()
	{
		mToAccount = mTransferTo.getText().toString();
		mFromAccount = mTransferFrom.getText().toString();
		mAmountToTransfer = mAmount.getText().toString();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		mTransferDate = sdf.format(new Date());
		try
		{
			mAmountToTransferD = Double.parseDouble(mAmountToTransfer); 
		} catch (NumberFormatException e)
		{
			// p did not contain a valid double
		}
		
		mTransferTask = new TransferTask();
		mTransferTask.execute((Void) null);
	}

	public class TransferTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			InputStream is = null;

			for (String IP : IP_ADDRESSES)
			{
				try
				{

					LoginActivity.client = Connection.getClient();

					Intent i = getIntent();

					// HttpClient client = new DefaultHttpClient(httpParams);
					HttpPost post = new HttpPost("http://" + IP
							+ "/user/transferfunds");
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-type", "application/json");
					post.setHeader("Auth-Token", i.getStringExtra("token"));
					
					// HttpResponse response = null;
					HttpEntity entity = null;

					Log.e("executing request", " " + post.getURI());

					String JSONResult = null;
					HttpResponse response = null;

					Thread.sleep(200);

					JSONObject transferObj = new JSONObject();
					try
					{
						transferObj.put("toAccount", mToAccount);
						transferObj.put("fromAccount", mFromAccount);
						transferObj.put("amount", mAmountToTransferD);
						transferObj.put("transferDate", mTransferDate);
						transferObj.put("transferNotes", "transfer");

					} catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					HashMap<String, Object> data = new HashMap<String, Object>();
					
					String hash = mToAccount + mFromAccount + mAmountToTransferD + mTransferDate + "transfer";
					try
					{
						hash = hashMac(hash, HMAC_KEY);
					} catch (SignatureException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					data.put("mac", hash);
					data.put("payload", transferObj);
					
					try
					{
						response = LoginActivity.client.execute(post);
					} catch (ClientProtocolException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Log.d("Response", response.toString());

				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					Log.e("Interrupted Exception", e.getMessage() + " on IP:"
							+ IP);
				}
			}

			return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(final Boolean success)
		{
			// mAccountsTask = null;
			// showProgress(false);

			Intent i;
			if (success)
			{
				// i = new Intent(DashboardActivity.this,
				// AccountViewActivity.class);
				// i.putStringArrayListExtra("accountsList", (ArrayList<String>)
				// accountsList);
				// startActivity(i);
			} else
			{

			}

		}

		@Override
		protected void onCancelled()
		{
			// mAccountsTask = null;
			// showProgress(false);
		}

	}

	/**
	 * Encryption of a given text using the provided secretKey
	 * 
	 * @param text
	 * @param secretKey
	 * @return the encoded string
	 * @throws SignatureException
	 */
	public static String hashMac(String text, String secretKey)
			throws SignatureException
	{

		try
		{
			Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
			Mac mac = Mac.getInstance(sk.getAlgorithm());
			mac.init(sk);
			final byte[] hmac = mac.doFinal(text.getBytes());
			return toHexString(hmac);
		} catch (NoSuchAlgorithmException e1)
		{
			// throw an exception or pick a different encryption method
			throw new SignatureException(
					"error building signature, no such algorithm in device "
							+ HASH_ALGORITHM);
		} catch (InvalidKeyException e)
		{
			throw new SignatureException(
					"error building signature, invalid key " + HASH_ALGORITHM);
		}
	}

	public static String toHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		Formatter formatter = new Formatter(sb);
		for (byte b : bytes)
		{
			formatter.format("%02x", b);
		}

		return sb.toString();
	}
	
	public static String encrypt(String data) {
	
		String encrypted_data = "";
		
		//Cipher cipher = Cipher.getInstance("AES/CBC/)
		
		return encrypted_data;
	}
	
	public static void setIV() {
		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		IV = new String(iv);
		
	}
}
