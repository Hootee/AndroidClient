package org.jyu.itks545;

import org.jyu.itks545.R.id;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

	private static final int UPDATE_LATLNG = 2;

	private LatLng location;
	
    private Handler mHandler;

	private final LocationListener listener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
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
        
		// Check if location services are enabled. Nothing to do with LocationService.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean gpsEnabled = true;
		boolean networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// Check if enabled and if not send user to the GPS settings
		if (!gpsEnabled || !networkEnabled) {
			showGPSDisabledAlertToUser();
		}
		
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				2 * 60 * 1000, 	// 2 minutes
				10,				// 10 meters
				listener);

		mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_LATLNG:
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
//			addMessage();
			Intent intent = new Intent(this, WriteMessageActivity.class);
			intent.putExtra("latitude", location.latitude);
			intent.putExtra("longitude", location.longitude);
			startActivity(intent);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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
	 * http://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-
	 * an-android-device-is-enabled
	 */
	private void showGPSDisabledAlertToUser() {
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
	
//	private void addMessage() {
//		Log.i(TAG, "addMessage");
//		WriteMessageFrag writeMessageFrag = new WriteMessageFrag();
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.replace(R.id.fragment_container, writeMessageFrag, "WriteMessageFrag");
//		fragmentTransaction.commit();		
//	}
}
