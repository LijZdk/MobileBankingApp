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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AccountViewActivity extends Activity {

	private ListView mListView;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_view_accounts);
		
		mListView = (ListView) findViewById(R.id.choice_container);
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
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
			
			HttpGet httpGet = new HttpGet("http://10.251.5.20:8888/user/authenticate");
			
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
			
			try
			{
				jsonObject = new JSONObject(stringBuilder.toString());
				
				//Need to know order of JSON Object
				String accountType = ((JSONArray)jsonObject.get("name")).getString(0);
				
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	
}
