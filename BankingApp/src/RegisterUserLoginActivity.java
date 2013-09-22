

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterUserLoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"user:basic:6666" };	
	
	/**
	 * Store the settings file name
	 */
	private String PREFS_NAME = "startup_settings.cfg";
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	
	// Value for username, password, pin
	private String mUsername, mPassword, mPin;	
	
	// UI references.
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private EditText mPinView;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_register_user);
		
		Intent i = this.getIntent();
		mUsername = i.getStringExtra("username");
		mPassword = i.getStringExtra("password");
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mPin = settings.getString("pin", "");
		
		mPinView = (EditText) findViewById (R.id.pin);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		if (settings.getBoolean("remember_me", false)) {
			editor.putString("pin", mPin);
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

		// Reset errors.
		mPinView.setError(null);

		// Store values at the time of the login attempt.
		mPin = mPinView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPin)) {
			mPinView.setError(getString(R.string.error_field_required));
			focusView = mPinView;
			cancel = true;
		} else if (mPin.length() != 4) {
			mPinView.setError(getString(R.string.error_invalid_password));
			focusView = mPinView;
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
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

//			HttpClient client = new DefaultHttpClient();
//			HttpPost post = new HttpPost("http://192.168.2.112/index.php");
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			
//			nameValuePairs.add(new BasicNameValuePair("username", "user"));
//			nameValuePairs.add(new BasicNameValuePair("password", "basic"));
//			
//			try {
//				post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			
//			HttpResponse response = null;
			
			try {
				// Simulate network access.
				Thread.sleep(2000);
//				response = client.execute(post);
//			} catch (ClientProtocolException e) {
//				return false;
//			} catch (IOException e) {
//				return false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return false;
			}
			
			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
					// Account exists, return true if the password matches.
					return pieces[2].equals(mPin);
			}
			
//			if (response.getEntity().toString() == "true") 
//				return true;

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
//				if (mRememberMe && mPin )
				Intent i = new Intent(RegisterUserLoginActivity.this, DashboardActivity.class);
				i.putExtra("username", mUsername);
				i.putExtra("password", mPassword);
				i.putExtra("pin", mPin);
				startActivity(new Intent(RegisterUserLoginActivity.this, DashboardActivity.class));
				finish();
			} else {
				mPinView
						.setError(getString(R.string.error_incorrect_password));
				mPinView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
}
