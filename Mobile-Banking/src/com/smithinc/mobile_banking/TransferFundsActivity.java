package com.smithinc.mobile_banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

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
import android.widget.Spinner;

public class TransferFundsActivity extends Activity
{
	Spinner spinner1, spinner2;

	EditText mAmount;
	Button mTransferButton;

	String mToAccount;
	String mFromAccount;
	String mAmountToTransfer;
	String mTransferDate;
	int mAmountToTransferFirst;
	float mAmountToTransferSecond;

	private TransferTask mTransferTask;

	private static final String[] IP_ADDRESSES =
	{
	/*
	 * "ec2-54-200-161-9.us-west-2.compute.amazonaws.com/webservices/"
	 * "129.252.226.44:8888"
	 */"ec2-54-201-49-238.us-west-2.compute.amazonaws.com"
	/*
	 * "192.168.1.76:8080" , "192.168.1.106:80", "10.251.4.220"
	 */
	};

	private static String HMAC_KEY = "065a62448fb75fce3764dcbe68f9908d";

	private static String AES_KEY = "b36013521d0f5dbea0e4ac1fd7af804a";

	private static String HASH_ALGORITHM = "HmacSHA256";

	private static String IV;

	private static String HASHED_MAC;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_transfer_funds);
		
		addListenerOnSpinnerItemSelection();
		
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

	public void addListenerOnSpinnerItemSelection()
	{
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		
	}

	public void transfer()
	{
		mToAccount = String.valueOf(spinner1.getSelectedItem());
		// mToAccount = mTransferTo.getText().toString();
		mFromAccount = String.valueOf(spinner2.getSelectedItem());
		// mFromAccount = mTransferFrom.setText("Savings");
		mAmountToTransfer = mAmount.getText().toString();

		Log.e("Spinner Check", "Check: To " + mToAccount + " From "
				+ mFromAccount);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		mTransferDate = sdf.format(new Date());
		try
		{
			mAmountToTransferFirst = (int) (Double
					.parseDouble(mAmountToTransfer) * 100);
			// mAmountToTransferF = Float.parseFloat(mAmountToTransfer);
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
							+ "/account/transferfunds");
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-type", "application/json");
					post.setHeader("Auth-Token", i.getStringExtra("token"));

					Log.e("Auth-Token ", "token: " + i.getStringExtra("token"));

					// HttpResponse response = null;
					HttpEntity entity = null;

					Log.e("executing request", " " + post.getURI());

					String JSONResult = null;
					HttpResponse response = null;

					Thread.sleep(200);

					HashMap<String, Object> data = new HashMap<String, Object>();
					// Put D back to get double.
					String hash = mToAccount + mFromAccount
							+ mAmountToTransferFirst + mTransferDate;

					Log.e("Hash", "string: " + hash);
					try
					{
						hash = hashMac(hash, HMAC_KEY);
					} catch (SignatureException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					data.put("mac", hash);
					// data.put("payload", transferObj);

					JSONObject transferObj = new JSONObject();
					JSONArray transJsonArray = new JSONArray();

					try
					{
						JSONObject reqObj = new JSONObject();
						reqObj.put("mac", hash);
						transJsonArray.put(reqObj);
						reqObj = new JSONObject();
						reqObj.put("toAccount", mToAccount);
						transJsonArray.put(reqObj);
						reqObj = new JSONObject();
						reqObj.put("fromAccount", mFromAccount);
						transJsonArray.put(reqObj);
						reqObj = new JSONObject();
						// Put D back to get double.
						reqObj.put("amount", mAmountToTransferFirst);
						transJsonArray.put(reqObj);
						reqObj = new JSONObject();
						reqObj.put("transferDate", mTransferDate);
						transJsonArray.put(reqObj);
						reqObj = new JSONObject();
						reqObj.put("transferNotes", "transfer");
						transJsonArray.put(reqObj);

						transferObj.put("payload", transJsonArray);

						Log.e("JSONArray: ",
								"Object: " + transferObj.toString());

					} catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					StringEntity transferStEntity = null;

					try
					{
						transferStEntity = new StringEntity(
								transferObj.toString());
					} catch (UnsupportedEncodingException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					post.setEntity(transferStEntity);

					try
					{
						response = LoginActivity.client.execute(post);
						Log.e("Response code: ", "code"
								+ response.getStatusLine().getStatusCode()
								+ " More of response: " + response.toString());

						entity = response.getEntity();
						is = entity.getContent();

						try
						{
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(is, "iso-8859-1"), 8);
							StringBuilder sb = new StringBuilder();
							String line = null;
							while ((line = reader.readLine()) != null)
							{
								sb.append(line + "\n");
							}
							is.close();
							JSONResult = sb.toString();
							Log.e("JSON Result", JSONResult);
						} catch (Exception e)
						{
							Log.e("Buffer Error", "Error converting result "
									+ e.toString());
						}

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

	public static String encrypt(String data)
	{

		String encrypted_data = "";

		// Cipher cipher = Cipher.getInstance("AES/CBC/)

		return encrypted_data;
	}

	public static void setIV()
	{
		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		IV = new String(iv);

	}

	private static JSONObject getJsonObjectFromMap(Map params)
			throws JSONException
	{

		// all the passed parameters from the post request
		// iterator used to loop through all the parameters
		// passed in the post request
		Iterator iter = params.entrySet().iterator();

		// Stores JSON
		JSONObject holder = new JSONObject();

		// using the earlier example your first entry would get email
		// and the inner while would get the value which would be 'foo@bar.com'
		// { fan: { email : 'foo@bar.com' } }

		// While there is another entry
		while (iter.hasNext())
		{
			// gets an entry in the params
			Map.Entry pairs = (Map.Entry) iter.next();

			// creates a key for Map
			String key = (String) pairs.getKey();

			// Create a new map
			Map m = (Map) pairs.getValue();

			// object for storing Json
			JSONObject data = new JSONObject();

			// gets the value
			Iterator iter2 = m.entrySet().iterator();
			while (iter2.hasNext())
			{
				Map.Entry pairs2 = (Map.Entry) iter2.next();
				data.put((String) pairs2.getKey(), (String) pairs2.getValue());
			}

			// puts email and 'foo@bar.com' together in map
			holder.put(key, data);
		}
		return holder;
	}
}
