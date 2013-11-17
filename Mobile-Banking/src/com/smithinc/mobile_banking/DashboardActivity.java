package com.smithinc.mobile_banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity
{

	/*
	 * Our accounts async task.
	 */
	private AccountsTask mAccountsTask;

	/*
	 * Where we store valid IP addresses
	 */
	private static final String[] IP_ADDRESSES =
	{
	/* "ec2-54-200-161-9.us-west-2.compute.amazonaws.com/webservices/" */
	"129.252.226.193:8888",
	/*
	 * "192.168.1.76:8080" , "192.168.1.106:80", "10.251.4.220"
	 */
	};

	// Is the device registered
	private boolean isRegistered;

	// Account name array
	private List accountsList;
	// static DefaultHttpClient client;
	private TextView mStatusMessageView;
	private View mStatusView;
	private View mDashboardView;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_dashboard);

		mStatusView = (View) findViewById(R.id.status);
		mDashboardView = (View) findViewById(R.id.dashboard);

		mStatusMessageView = (TextView) findViewById(R.id.status_message);

		mListView = (ListView) findViewById(R.id.dashboard_container);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Intent i;
				switch (position)
				{
				case 0:
					mStatusMessageView
							.setText(R.string.progress_retrieving_data);
					showProgress(true);
					mAccountsTask = new AccountsTask();
					mAccountsTask.execute((Void) null);
					break;
				case 1:
					i = new Intent(DashboardActivity.this,
							TransferFundsActivity.class);
					Intent j = getIntent();
					
					i.putExtra("username", j.getStringExtra("username"));
					i.putExtra("password", j.getStringExtra("password"));
					i.putExtra("token", j.getStringExtra("token"));
					startActivity(i);
					break;
				case 2:
					// AsyncTask signout

					if (!isRegistered)
					{
						i = new Intent(DashboardActivity.this,
								LoginActivity.class);
					} else
					{
						i = new Intent(DashboardActivity.this,
								RegisteredUserLoginActivity.class);
					}
					startActivity(i);
					finish();
					break;
				}
			}
		});

	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show)
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mDashboardView.setVisibility(View.VISIBLE);
			mDashboardView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mDashboardView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mDashboardView.setVisibility(show ? View.VISIBLE : View.GONE);
			mDashboardView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class AccountsTask extends AsyncTask<Void, Void, Boolean>
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
					HttpGet get = new HttpGet("http://" + IP + "/user/accounts");
					get.setHeader("Accept", "application/json");
					get.setHeader("Content-type", "application/json");
					get.setHeader("Auth-Token", i.getStringExtra("token"));
					HttpResponse response = null;
					HttpEntity entity = null;

					Log.e("executing request", " " + get.getURI());

					String JSONResult = null;

					try
					{
						Thread.sleep(200);
						response = LoginActivity.client.execute(get/*
																	 * ,
																	 * localContext
																	 */);
						Log.e("Response", ""
								+ response.getStatusLine().getStatusCode());
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

						JSONObject jObject = new JSONObject(JSONResult);

						JSONArray jArray = jObject.toJSONArray(null);

						accountsList = new ArrayList();

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

						accountsList.add(accTypChk);
						accountsList.add(accNumChk);
						accountsList.add(accBalChk);

						accountsList.add(accTypSav);
						accountsList.add(accNumSav);
						accountsList.add(accBalSav);

						accountsList.add(accTypRet);
						accountsList.add(accNumRet);
						accountsList.add(accBalRet);

						Log.e("Accounts type", " Type: " + accTypSav
								+ " Balance: " + accBalSav);
						Log.e("Accounts type", " Type: " + accTypChk
								+ " Balance: " + accBalChk);
						Log.e("Accounts type", " Type: " + accTypRet
								+ " Balance: " + accBalRet);
						if (response != null
								&& response.getStatusLine().getStatusCode() == 200)
							return true;

					} catch (JSONException e)
					{
						Log.e("JSON Parser", "Error parsing data");
					}

					// String result = EntityUtils.toString(entity);

					// Log.d("Results", result);

				} catch (ClientProtocolException e)
				{
					// TODO Auto-generated catch block
					Log.e("Client ProtocolException", e.getMessage()
							+ " on IP:" + IP);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					Log.e("IOException", e.getMessage() + " on IP:" + IP);
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
			mAccountsTask = null;
			showProgress(false);

			Intent i;
			if (success)
			{
				i = new Intent(DashboardActivity.this,
						AccountViewActivity.class);
				i.putStringArrayListExtra("accountsList",
						(ArrayList<String>) accountsList);

				startActivity(i);
			} else
			{

			}

		}

		@Override
		protected void onCancelled()
		{
			mAccountsTask = null;
			showProgress(false);
		}

	}
}
