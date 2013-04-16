package org.jyu.itks545;

import org.jyu.itks545.R.id;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

public class MyMapActivity extends FragmentActivity {
	private static final String TAG = MyMapActivity.class.getSimpleName();
	
	private static final int LOGIN_REQUEST_CODE = 1234;
	
	
	private int mUserID;
	private String mAuthorizedAccessToken;
	private String mAuthorizedAccessTokenSecret;
//	private String mUsername;
	private String mConsumerKey;
	private String mConsumerSecret;
	
	private static final int UPDATE_LATLNG = 2;

	// Current location.
	private LatLng location;
	
	// Message handler.
    private Handler mHandler;

	private final LocationListener listener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
	    public void onLocationChanged(Location location) {
	        // A new location update is received.  Do something useful with it.  In this case,
	        // we're sending the update to a handler which then updates the UI with the new
	        // location.
	        Message.obtain(mHandler,
	                UPDATE_LATLNG,
	                new LatLng(location.getLatitude(), location.getLongitude())).sendToTarget();

	        }

	};
	
	private LocationManager mLocationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_mymap);

		// Restore apps state (if exists) after rotation.
        if (savedInstanceState != null) {
            Double latitude = savedInstanceState.getDouble("latitude");
            Double longitude = savedInstanceState.getDouble("longitude");
            if (latitude != 0 || longitude != 0) {
            	location = new LatLng(latitude, longitude);
            }
        } else {
        }
        
        // if we have userID and accessToken we are good to go.
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        mUserID = preferences.getInt("userID", 0);
        mAuthorizedAccessToken = preferences.getString("authorizedAccessToken", null);
        mAuthorizedAccessTokenSecret = preferences.getString("authorizedAccessTokenSecret", null);
        mConsumerKey = preferences.getString("consumerKey", null);
        mConsumerSecret = preferences.getString("consumerSecret", null);

		// Check if location services are enabled. Nothing to do with LocationService.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// We are using only network-based location.
//		boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean gpsEnabled = true;
		boolean networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//		boolean networkEnabled = true;

		// Check if enabled and if not send user to the GPS settings
		if (!gpsEnabled || !networkEnabled) {
			showLocationDisabledAlertToUser();
		}
		
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				2 * 60 * 1000, 	// 2 minutes
				10,				// 10 meters
				listener);

		mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_LATLNG:
                    	// When first location is received, we move the camera to that location.
                    	boolean firstFix = false;
                    	if (location == null) {
                    		firstFix = true;
                    	}
                        location = (LatLng) msg.obj;
                        if (firstFix) {
                        	MyMapFragment mapFrag = (MyMapFragment) getSupportFragmentManager().findFragmentByTag("MapFrag");
                        	mapFrag.setPosition(location);
                        }
                        break;
                }
            }
        };

        createMap();
	}
	
	/*
	 * We create a map if there is no map already.
	 */
	public void createMap() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.findFragmentByTag("MapFrag") == null) {
			Log.d(TAG, this + ": Existing fragment not found.");
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_container, new MyMapFragment(), "MapFrag");
			fragmentTransaction.commit();
		} else {
			Log.d(TAG, this + ": Existing fragment found.");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.action_add_message:
			if(mUserID != 0 && mAuthorizedAccessToken != null && mConsumerKey != null && mConsumerSecret != null) {
				Intent intent = new Intent(this, WriteMessageActivity.class);
				intent.putExtra("latitude", location.latitude);
				intent.putExtra("longitude", location.longitude);
				intent.putExtra("userID", mUserID);
				intent.putExtra("consumerKey", mConsumerKey);
				intent.putExtra("consumerSecret", mConsumerSecret);
				intent.putExtra("accessToken", mAuthorizedAccessToken);
				intent.putExtra("accessTokenSecret", mAuthorizedAccessTokenSecret);

				startActivity(intent);
			} else {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, LOGIN_REQUEST_CODE);				
			}
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Get the results from the login intent and put them to shared preferences.
		if (resultCode == RESULT_OK && requestCode == LOGIN_REQUEST_CODE) {
		  Bundle returnedData = data.getExtras();
		  mUserID = returnedData.getInt(LoginActivity.USERID);
		  mConsumerKey = returnedData.getString(LoginActivity.CONSUMERKEY);
		  mConsumerSecret = returnedData.getString(LoginActivity.CONSUMERSECRET);
		  mAuthorizedAccessToken = returnedData.getString(LoginActivity.AUTHORIZEDACCESSTOKEN);
		  mAuthorizedAccessTokenSecret = returnedData.getString(LoginActivity.AUTHORIZEDACCESSTOKENSECRET);
		  Log.d(TAG, "UserID: " + mUserID + ", ConsumerKey: " + mConsumerKey + ", ConsumerSecret: " + mConsumerSecret + ", mAuthorizedAccessToken: " + mAuthorizedAccessToken + ", mAuthorizedAccessTokenSecret: " + mAuthorizedAccessTokenSecret);
		  SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		  SharedPreferences.Editor editor = preferences.edit();
		  editor.putInt("userID", mUserID);
		  editor.putString("consumerKey", mConsumerKey);
		  editor.putString("consumerSecret", mConsumerSecret);
		  editor.putString("authorizedAccessToken", mAuthorizedAccessToken);
		  editor.putString("authorizedAccessTokenSecret", mAuthorizedAccessTokenSecret);
		  editor.commit();
	  }
	  
	  
	  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (location != null) {
			outState.putDouble("latitude", location.latitude);
			outState.putDouble("longitude", location.longitude);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	/*
	 * Show location settings.
	 * 
	 * http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-
	 * an-android-device-is-enabled
	 */
	private void showLocationDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(R.string.enable_GPS)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
	
}
