package com.smithinc.mobile_banking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DashboardActivity extends Activity {

	// Is the device registered
	private boolean isRegistered;
	
	private ListView mListView;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_dashboard);
	
		mListView = (ListView) findViewById(R.id.choice_container);
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
				Intent i;
				switch (position) {
					case 0:
						i = new Intent(DashboardActivity.this, AccountViewActivity.class);
						startActivity(i);
						break;
					case 1:
						i = new Intent(DashboardActivity.this, TransferFundsActivity.class);
						startActivity(i);
						break;
					case 2:
						// AsyncTask signout
						
						if (!isRegistered) {
							i = new Intent(DashboardActivity.this, LoginActivity.class);
						} else {
							i = new Intent(DashboardActivity.this, RegisteredUserLoginActivity.class);
						}
						startActivity(i);
						finish();						
						break;						
				}
			}
		});
		
	}
	
}
