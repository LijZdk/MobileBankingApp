package com.smithinc.mobile_banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

	private TransferTask mTransferTask;

	private static final String[] IP_ADDRESSES =
	{
	/* "ec2-54-200-161-9.us-west-2.compute.amazonaws.com/webservices/" */
	"129.252.226.44:8888",
	/*
	 * "192.168.1.76:8080" , "192.168.1.106:80", "10.251.4.220"
	 */
	};

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
							+ "/user/authenticate");
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-type", "application/json");
					post.setHeader("Auth-Token", i.getStringExtra("token"));
					//HttpResponse response = null;
					HttpEntity entity = null;

					Log.e("executing request", " " + post.getURI());

					String JSONResult = null;

					try
					{
						Thread.sleep(200);

						JSONObject jObject = new JSONObject(JSONResult);

						JSONArray jArray = jObject.toJSONArray(null);

						// accountsList = new ArrayList();

						String accTypChk = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(0)
								.getString("name");
						int accNumChk = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(0)
								.getInt("number");
						double accBalChk = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(0)
								.getDouble("balance");

						String accTypSav = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(1)
								.getString("name");
						int accNumSav = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(1)
								.getInt("number");
						double accBalSav = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(1)
								.getDouble("balance");

						String accTypRet = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(2)
								.getString("name");
						int accNumRet = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(2)
								.getInt("number");
						double accBalRet = ((JSONArray) jObject
								.get("unencrypted payload")).getJSONObject(2)
								.getDouble("balance");

						// accountsList.add(accTypChk);
						// accountsList.add(accNumChk);
						// accountsList.add(accBalChk);
						//
						// accountsList.add(accTypSav);
						// accountsList.add(accNumSav);
						// accountsList.add(accBalSav);
						//
						// accountsList.add(accTypRet);
						// accountsList.add(accNumRet);
						// accountsList.add(accBalRet);

						Log.e("Accounts type", " Type: " + accTypSav
								+ " Balance: " + accBalSav);
						Log.e("Accounts type", " Type: " + accTypChk
								+ " Balance: " + accBalChk);
						Log.e("Accounts type", " Type: " + accTypRet
								+ " Balance: " + accBalRet);
//						if (response != null
//								&& response.getStatusLine().getStatusCode() == 200)
//							return true;

					} catch (JSONException e)
					{
						Log.e("JSON Parser", "Error parsing data");
					}

					// String result = EntityUtils.toString(entity);

					// Log.d("Results", result);

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
}
