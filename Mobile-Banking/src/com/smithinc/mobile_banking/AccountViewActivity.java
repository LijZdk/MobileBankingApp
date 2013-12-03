package com.smithinc.mobile_banking;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.Format;
import java.util.ArrayList;
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
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AccountViewActivity extends Activity
{

	private ListView mListView;
	private ArrayAdapter<String> accAdt;
	private List<String> accListFView;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_accounts);

		mListView = (ListView) findViewById(R.id.accounts_container);

		List accList = new ArrayList<String>();
		accList = getIntent().getStringArrayListExtra("accountsList");

		accListFView = new ArrayList<String>();

		for (int i = 0; i < accList.size(); i=i+3)
		{
			// String typesList = "Type:        Number:       Balance:";
			String data = accList.get(i).toString() + "\n" + "Number: "
					+ accList.get(i+1).toString() + "\nBalance: "
					+ accList.get(i+2).toString();
			
			accListFView.add(data);
		}

		// accListFView.add(typesList);

		accAdt = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, accListFView);

		mListView.setAdapter(accAdt);
		
	}
}
