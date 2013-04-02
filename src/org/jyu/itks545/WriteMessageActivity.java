package org.jyu.itks545;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class WriteMessageActivity extends FragmentActivity {
	@SuppressWarnings("unused")
	private static final String TAG = WriteMessageActivity.class.getSimpleName();

	private LatLng location;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_writemessage);
		
		// Restore apps state (if exists) after rotation.
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Double latitude = extras.getDouble("latitude");
            Double longitude = extras.getDouble("longitude");
            if (latitude != 0 || longitude != 0) {
            	location = new LatLng(latitude, longitude);
            }
        } else {
        }
	}
	
	public void onClick(View v) {
		int buttonID = v.getId();
		switch (buttonID) {
		case R.id.buttonSend:
			Log.i(TAG, "buttonSend");
			EditText editText = (EditText) findViewById(R.id.username_register);
			String message = editText.getText().toString();
			List<NameValuePair> data = new ArrayList<NameValuePair>(4);
	        data.add(new BasicNameValuePair("userID", "1"));
	        data.add(new BasicNameValuePair("latitude", Double.toString(location.latitude)));
	        data.add(new BasicNameValuePair("longitude", Double.toString(location.longitude)));
	        data.add(new BasicNameValuePair("message", message));

	        new GetJsonASync(null, getString(R.string.server) + "messages/add/", data).execute();
			Log.i(TAG, "" + location.latitude);
			Intent intent1 = new Intent(this, MyMapActivity.class);
			startActivity(intent1);
			break;
		case R.id.buttonCancel:
			Log.i(TAG, "buttonCancel");
			Intent intent2 = new Intent(this, MyMapActivity.class);
			startActivity(intent2);
			break;

		default:
			break;
		}	
	}
}
