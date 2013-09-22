

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.net.http.*;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"user:basic:6666" };

	/**
	 * The default username to populate the username field with.
	 */
	public static final String EXTRA_USERNAME = "user";
	
	/**
	 * Store the settings file name
	 */
	public static final String PREFS_NAME = "startup_settings.cfg";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for username and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;
	
	// Values for 'remember me' boolean
	private boolean mRememberMe;
	
	// Is the device registered
	private boolean isRegistered;
	
	
	// Does the user already have a pin?
	private boolean mHasPin = false;
	
	// Boolean for pin authentication
	private boolean mPinAuth = true;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private Switch mRememberMeSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.getBoolean("remember_me", false)) {
			mUsername = settings.getString("username", "");
			mPassword = settings.getString("password", "");
			mRememberMe = settings.getBoolean("remember_me", false);
//			mPin = settings.getString(" " + mUsername, "");
		}

		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   
		
		if (!isRegistered)
			setContentView(R.layout.activity_login);
		else
			setContentView(R.layout.activity_registered_user_login);

		// Set up the login form.
		//mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		
		mRememberMeSwitch = (Switch) findViewById(R.id.remember_me_switch);
		mRememberMeSwitch.setChecked(mRememberMe);
		mRememberMeSwitch
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked)
								mRememberMe = true;
							else
								mRememberMe = false;
						}
					});
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
/*						final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
						final SharedPreferences.Editor editor = settings.edit();
						
						if (mRememberMe && settings.getString("pin", "") == "") {
							AlertDialog.Builder alert = new AlertDialog.Builder(context);
							alert.setTitle("Register a Pin");
							alert.setMessage("Enter Pin:");
							
							final EditText input = new EditText(context);
							alert.setView(input);
							
							alert.setPositiveButton("Set Pin", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									mPin = input.getText().toString();
									if (!mPin.contains(" ") && mPin.length() == 4) {
										editor.putString("pin", mPin);
										editor.commit();
										mPinAuth = true;
									}
									return;
								}
							});
							
							alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mPinAuth = false;
									return;									
								}
							});
							
							alert.show();
							
						} else {
							AlertDialog.Builder alert = new AlertDialog.Builder(context);
							alert.setTitle("Pin Login");
							alert.setMessage("Enter Pin:");
							
							final EditText input = new EditText(context);
							input.setInputType(InputType.TYPE_CLASS_NUMBER);
							alert.setView(input);
							
							alert.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int whichButton) {
									mPin = input.getText().toString();
									if (mPin.equals(settings.getString("pin", ""))) {
										mPinAuth = true;
									}
									return;
								}
							});
							
							alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mPinAuth = false;
									return;									
								}
							});
							
							alert.show();
							
						}*/
						attemptLogin();
						
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		if (mRememberMe) {
			editor.putString("username", mUsername);
			editor.putString("password", mPassword);
			editor.putBoolean("remember_me", mRememberMe);
		} else {
			editor.putString("username", "");
			editor.putString("password", "");
			editor.putBoolean("remember_me",  false);
		}
		
		editor.commit();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}
		
		if (!mPinAuth) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid username address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} else if (mUsername.contains(" ")) {
			mUsernameView.setError(getString(R.string.error_invalid_username));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Shows the pin login view.
	 */
	
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			
			Log.d("Username", mUsername);
			Log.d("Password", mPassword);
			
			//JSONParser jParser = new JSONParser();
			
			int response = -1;
			
			URL obj = null;
			try {
				obj = new URL("http://129.252.226.221:8888/user/authenticate");
			} catch (MalformedURLException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			HttpURLConnection connection = null;
			
			try {
				connection = (HttpURLConnection) obj.openConnection();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			};
			
			try {
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((mUsername + ":" + mPassword).getBytes(), Base64.NO_WRAP));
				connection.setDoOutput(true);
			} catch (ProtocolException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			
			try {
				response = connection.getResponseCode();
				Log.e("Response Code", "" + connection.getResponseCode());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			connection.disconnect();
			
			
			
			JSONObject object = new JSONObject();
			try {
				object.put("PHP_AUTH_USER", mUsername);
				object.put("PHP_AUTH_PW", mPassword);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
//			HttpClient client = new DefaultHttpClient();
//			
//			//HttpClient client = new DefaultHttpClient();
//			HttpPost post = new HttpPost("http://129.252.226.221:8888/user/authenticate");
//			post.setHeader("PHP_AUTH_USER", mUsername);
//			post.setHeader("PHP_AUTH_PW", mPassword);
//			
//			
//			
//			String responseBody = "";
//			
//			//httpPost.setHeader("Authroization",)
//			//httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
//			
//			
//			HttpResponse response = null;
//			
////			try {
////				
////			JSONObject obj = new JSONObject();
////			
////			obj.put("username", mUsername);
////			obj.put("password", mPassword);
//			
//			
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			
//			nameValuePairs.add(new BasicNameValuePair("user", mUsername));
//			nameValuePairs.add(new BasicNameValuePair("pass", mPassword));
//			
////			try {
////				post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
////			} catch (UnsupportedEncodingException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
////			
////			response = client.execute(post);
////			Log.d("Response", "" + response.getStatusLine().getStatusCode());
////			//responseBody = inputStreamToString(response.getEntity());
////			//Log.d("Response Entity", responseBody);
////			
////			} catch (JSONException e) {
////				Log.e("JSON Exception", e.getMessage());
////			} catch (ClientProtocolException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			
//			try {
//				post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			
//			
//			try {
//				// Simulate network access.
//				Thread.sleep(200);
//				response = client.execute(post);
//				Log.e("Response", "" + response.getStatusLine().getStatusCode());
//			} catch (ClientProtocolException e) {
//				Log.e("Client Protocol Exception", e.getMessage());
//				return false;
//			} catch (IOException e) {
//				Log.e("I/O Exception", e.getMessage());
//				return false;
//			} catch (InterruptedException e) {
//				Log.e("Interrupted Exception", e.getMessage());
//				// TODO Auto-generated catch block
//				return false;
//			}
//
////			for (String credential : DUMMY_CREDENTIALS) {
////				String[] pieces = credential.split(":");
////				if (pieces[0].equals(mUsername)) {
////					// Account exists, return true if the password matches.
////					return pieces[1].equals(mPassword);
////				}
////			}
//			
			if (response == 200) 
				return true;
			
			
			//client.getConnectionManager().shutdown();			

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
//				if (mRememberMe && mPin )
				Intent i;
				if (mHasPin/* && registeredDevice*/) {
					i = new Intent(LoginActivity.this, RegisteredUserLoginActivity.class);
					i.putExtra("username", mUsername);
					i.putExtra("password", mPassword);
				} else if (mHasPin) {
					i = new Intent(LoginActivity.this, RegisterUserLoginActivity.class);
					i.putExtra("username", mUsername);
					i.putExtra("password", mPassword);
				} else {
					i = new Intent(LoginActivity.this, DashboardActivity.class);
					i.putExtra("username", mUsername);
					i.putExtra("password", mPassword);
				}
				startActivity(i);
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
