package org.jyu.itks545;

import org.jyu.itks545.R.string;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
			EditText editText = (EditText) findViewById(R.id.editText1);
			String message = editText.getText().toString();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(getString(R.string.server)).append("messages/add/")
			.append(location.longitude).append("/").append(location.latitude).append("/").append("100")
			.append("/").append(message);
			new GetJsonASync(null, stringBuilder.toString()).execute();
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
