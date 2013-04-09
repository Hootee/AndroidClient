package org.jyu.itks545;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jyu.itks545.MyOAuth.AccessToken;
import org.jyu.itks545.MyOAuth.Authorize;
import org.jyu.itks545.MyOAuth.RequestToken;

import android.R.bool;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	public static String USERID = "userid";
	public static String CONSUMERKEY = "consumerkey";
	public static String CONSUMERSECRET = "consumersecret";
	public static String AUTHORIZEDACCESSTOKEN = "authorizedaccesstoken";
	public static String AUTHORIZEDACCESSTOKENSECRET = "authorizedaccesstokensecret";
	
	private RequestToken requestToken;
	private Authorize authorizer;
	private AccessToken accessToken;
	
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	public static final String EXTRA_USERNAME = "";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mLoginTask = null;
	private UserAuthorizationTask mAuthTask = null;
	
	private int mUserID;
	private String mAuthorizedAccessToken;
	private String mAuthorizedAccessTokenSecret;
	private String mConsumerKey;
	private String mConsumerSecret;

	// Values for email and password at the time of the login attempt.
	private String mUsername;
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setupActionBar();

		// if we have userID and accessToken we are good to go.
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		mUserID = preferences.getInt("userID", 0);
		mAuthorizedAccessToken = preferences.getString("authorizedAccessToken", null);
		mConsumerKey = preferences.getString("consumerKey", null);
		mConsumerSecret = preferences.getString("consumerSecret", null);
		
		if (mAuthorizedAccessToken != null) {
			finish();
		}
		
		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
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

		findViewById(R.id.register_user_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startRegistration();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void startRegistration() {
//		String url = getString(R.string.server) + "register_user";
		String url = "http://192.168.100.41/itks545/register_user";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mLoginTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mEmail = mEmailView.getText().toString();
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

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		} else if (mUsername.contains("@")) {
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
			mLoginTask = new UserLoginTask();
			mLoginTask.execute((Void) null);
		}
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		data.putExtra(LoginActivity.USERID, mUserID);
		data.putExtra(LoginActivity.CONSUMERKEY, mConsumerKey);
		data.putExtra(LoginActivity.CONSUMERSECRET, mConsumerSecret);
		data.putExtra(LoginActivity.AUTHORIZEDACCESSTOKEN, mAuthorizedAccessToken);
		data.putExtra(LoginActivity.AUTHORIZEDACCESSTOKENSECRET, mAuthorizedAccessTokenSecret);
		setResult(RESULT_OK, data);
		super.finish();
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
	 * Represents an asynchronous login task used to authenticate
	 * the user in application.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, HttpResponse> {	
		
		@Override
		protected HttpResponse doInBackground(Void... params) {

			Log.i("UserLoginTask", "mUsername: " + mUsername + ", mPassword: " + mPassword);
						
			List<NameValuePair> data = new ArrayList<NameValuePair>(2);
	        data.add(new BasicNameValuePair("users_name", mUsername));
	        data.add(new BasicNameValuePair("users_password", mPassword));
	        
			return loginUser(getString(R.string.server) + getString(R.string.login), data);
		}

		private HttpResponse loginUser(String url, List<NameValuePair> data) {
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			HttpPost httpPost = new HttpPost(url);
			StringEntity entity = null;
			
			try {
	        	if (data != null) {
	        		entity = new UrlEncodedFormEntity(data);
	        		httpPost.setEntity(entity);
	        	}
	        } catch (UnsupportedEncodingException e) {
	        	Log.e("UserLoginTask", "HttpUtils : UnsupportedEncodingException : " + e);
	        }
			
			try {
				return client.execute(httpPost);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				client.close();
			}
		}
		

		/**
		 * Parse login json output to variables.
		 * @param json
		 * @return
		 */
		private boolean parseLoginOutput(String json) {
			Log.i("UserLoginTask", json);
			try {
				JSONObject o = new JSONObject(json);
				mUserID = o.getInt("user_id");
				mConsumerKey = o.getString("consumer_key");
				mConsumerSecret = o.getString("consumer_secret");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}
	    
		/**
	     * Parse string from httpResponse.
	     * @param result
	     * @return
	     */
	    private String buildResponseString(HttpResponse result) {
			StringBuilder sb = new StringBuilder();
			String line = null;

			try {
				InputStream inputStream = result.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
				inputStream.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	    	return sb.toString();
	    }
	    
		@Override
		protected void onPostExecute(HttpResponse result) {
			mLoginTask = null;
			showProgress(false);
			boolean success = false;
			
			String responseString= null;
			try {
				responseString = buildResponseString(result);
				Log.i("UserLoginTask", "responseString: " + responseString);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			success = parseLoginOutput(responseString);
					
			
			if (success) {
				Log.i("UserLoginTask", "success");
				mAuthTask = new UserAuthorizationTask();
				mAuthTask.execute((Void) null);
			} else {
				Log.i("UserLoginTask", "failure");
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mLoginTask = null;
			showProgress(false);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserAuthorizationTask extends AsyncTask<Void, Void, Boolean> {	
		
		@Override
		protected Boolean doInBackground(Void... params) {

			Log.i("UserAuthorizationTask", "mUserID: " + mUserID + ", mConsumerKey: " + mConsumerKey + ", mConsumerSecret: " + mConsumerSecret);
			
			requestToken = new RequestToken(mConsumerKey, mConsumerSecret);
			try {
				requestToken.sendRequest();
			} catch (Exception e) {
				// TODO Automaattisesti luotu catch-lohko
				e.printStackTrace();
			}
			
			authorizer = new Authorize(mConsumerKey, mConsumerSecret, requestToken);
			
			List<NameValuePair> data = new ArrayList<NameValuePair>(4);
	        data.add(new BasicNameValuePair("user_id", Integer.toString(mUserID)));
	        data.add(new BasicNameValuePair(LoginActivity.CONSUMERKEY, mConsumerKey));
	        data.add(new BasicNameValuePair(LoginActivity.CONSUMERSECRET, mConsumerSecret));
	        data.add(new BasicNameValuePair("oauth_token", requestToken.getResponseString("oauth_token")));
	        
			HttpResponse result = authorizeUser(authorizer.getAuthorizationURL(), data);
			
			String responseString= null;
			
			try {
				responseString = buildResponseString(result);
				Log.i("UserAuthorizationTask", "responseString: " + responseString);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String pin = parsePinJsonOutput(responseString);
			boolean success = false;
			
			if (pin != null) {
				accessToken = new AccessToken(mConsumerKey, mConsumerSecret, pin, requestToken);
				try {
					accessToken.sendRequest();
					mAuthorizedAccessToken = accessToken.getResponseString("oauth_token");
					mAuthorizedAccessTokenSecret = accessToken.getResponseString("oauth_token_secret");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				success = true;
			}
			return success;
		}

		private HttpResponse authorizeUser(String url, List<NameValuePair> data) {
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			HttpPost httpPost = new HttpPost(url);
			StringEntity entity = null;
			
			try {
	        	if (data != null) {
	        		entity = new UrlEncodedFormEntity(data);
	        		httpPost.setEntity(entity);
	        	}
	        } catch (UnsupportedEncodingException e) {
	        	Log.e("UserAuthorizationTask", "UnsupportedEncodingException : " + e);
	        }
			
			try {
				return client.execute(httpPost);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				client.close();
			}
		}
		
		/**
		 * Parse login json output to variables.
		 * @param json
		 * @return
		 */
		private String parsePinJsonOutput(String json) {
			Log.i("UserAuthorizationTask", json);
			String pin = null;
			try {
				JSONObject o = new JSONObject(json);
				pin = o.getString("pin");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return pin;
		}
	    
		/**
	     * Parse string from httpResponse.
	     * @param result
	     * @return
	     */
	    private String buildResponseString(HttpResponse result) {
			StringBuilder responseString = new StringBuilder();
			String line = null;

			try {
				InputStream inputStream = result.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				while ((line = bufferedReader.readLine()) != null) {
					responseString.append(line);
				}

				bufferedReader.close();
				inputStream.close();
				inputStreamReader.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	    	return responseString.toString();
	    }	    
		@Override
		protected void onPostExecute(Boolean success) {
			mAuthTask = null;
			showProgress(false);
						
			if (success) {
				Log.i("UserAuthorizationTask", "success");
				finish();
			} else {
				Log.i("UserAuthorizationTask", "failure");
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
