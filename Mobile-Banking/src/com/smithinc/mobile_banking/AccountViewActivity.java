package com.smithinc.mobile_banking;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
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
		
//		mListView = (ListView) findViewById(R.id.accounts_container);
//		
//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
//			}
//		});
		
		
		
		/*public void addItems(View v) {
			
<<<<<<< HEAD
=======
			HttpGet httpGet = new HttpGet("http://129.252.226.221:8888/user/accounts");
>>>>>>> origin/master
			
		}*/
		
		
		@Override
		protected void onPostExecute(List result)
		{
			
		}
		
	}
	
	
}
