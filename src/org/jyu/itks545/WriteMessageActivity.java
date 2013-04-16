package org.jyu.itks545;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jyu.itks545.MyOAuth.AddMessage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

public class WriteMessageActivity extends FragmentActivity {
	@SuppressWarnings("unused")
	private static final String TAG = WriteMessageActivity.class.getSimpleName();

	private Double latitude, longitude;
	private int userID;
	private String consumerKey, consumerSecret, accessToken, accessTokenSecret;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_writemessage);
		
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
            userID = extras.getInt("userID");
            consumerKey = extras.getString("consumerKey");
            consumerSecret = extras.getString("consumerSecret");
            accessToken = extras.getString("accessToken");
            accessTokenSecret = extras.getString("accessTokenSecret");
        }
 	}
	
	public void onClick(View v) {
		int buttonID = v.getId();
		switch (buttonID) {
		case R.id.buttonSend:
			new AddMessageASync().execute((Void) null);
			break;

		case R.id.buttonCancel:
			break;

		default:
			break;
		}	
		
		// We go back to Map in every case.
		Intent intent1 = new Intent(this, MyMapActivity.class);
		startActivity(intent1);
	}
	
	/**
	 * This sends message to the server on the background.
	 * @author tonsal
	 *
	 */
	private class AddMessageASync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			EditText editText = (EditText) findViewById(R.id.editTextMessage);
			try {
				String message = URLEncoder.encode(editText.getText().toString(), "UTF-8");
				AddMessage addMessage = new AddMessage(
						userID, 
						latitude, 
						longitude, 
						message, 
						consumerKey, 
						consumerSecret, 
						accessToken, 
						accessTokenSecret);

				addMessage.sendRequest();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
