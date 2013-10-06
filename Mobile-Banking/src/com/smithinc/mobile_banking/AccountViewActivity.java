package com.smithinc.mobile_banking;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smithinc.mobile_banking.R;
import com.smithinc.mobile_banking.LoginActivity.UserLoginTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AccountViewActivity extends Activity {

	private ListView mListView;
	private  grabAccountInfoTask grabInfoTask = null;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_view_accounts);
		
		mListView = (ListView) findViewById(R.id.account_container);
		
		grabInfoTask = new grabAccountInfoTask();
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
				
				grabInfoTask.execute();
			}
		});	
	}
	
	public class grabAccountInfoTask extends AsyncTask<Void, Void, List>
	{

		@Override
		protected List doInBackground(Void... params)
		{
			StringBuilder stringBuilder;
			String account, balance;
			

			HttpGet httpGet = new HttpGet("http://192.168.1.106:80/user/accounts");
			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			
			stringBuilder = new StringBuilder();
			
			try
			{
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				
				while((b = stream.read()) != -1)
				{
					stringBuilder.append((char) b);
				}
			} 
			catch (ClientProtocolException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JSONObject jsonObject;
			
			//try
			//{
				//jsonObject = new JSONObject(stringBuilder.toString());
				
				//Need to know order of JSON Object
				//String accountType = ((JSONArray)jsonObject.get("iv")).getString(0);
				Log.e("Accounts type"," Type: " + stringBuilder.toString());
				
			//}
			//catch(JSONException e)
			//{
			//	e.printStackTrace();
			//}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(List result)
		{
			
		}
		
	}
	
	
}
