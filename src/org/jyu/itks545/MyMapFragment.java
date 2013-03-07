package org.jyu.itks545;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapFragment extends SupportMapFragment implements AsyncCallback, OnMarkerClickListener {
	@SuppressWarnings("unused")
	private static final String TAG = MyMapFragment.class.getSimpleName();
	
	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */
	private GoogleMap mMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		mMap = getMap();
		mMap.setOnMarkerClickListener(this);
		initMap();
		getAllMarkers();
	}

	private void initMap() {
		UiSettings settings = mMap.getUiSettings();
		settings.setAllGesturesEnabled(true);
		settings.setMyLocationButtonEnabled(false);
	}


	public void setPosition(LatLng latLng) {
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
	}
	
	private void getAllMarkers() {
		StringBuilder querySB = new StringBuilder();
		querySB.append(getString(R.string.server)).append("messages/getall");
		new GetJsonASync(this, querySB.toString()).execute();
	}

	/*
	 * Show markers on map.
	 * Its called from GetAllMarkersASync() as a callback.
	 */
	private void showMarkers(String json) {
		Log.i(TAG, json);
		try {
			JSONObject jObj = new JSONObject(json);
			JSONArray jArray = new JSONArray();
			jArray = jObj.getJSONArray("messages");
			for(int i = 0; i < jArray.length(); i++) {
				JSONObject o = jArray.getJSONObject(i);
				mMap.addMarker(createMarker(o));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private MarkerOptions createMarker(JSONObject o) throws JSONException {
		int userID = o.getInt("userID");
		double longitude = o.getDouble("longitude");
		double latitude = o.getDouble("latitude");
		String text = o.getString("text");
		LatLng latLng = new LatLng(latitude, longitude);
		Log.i(TAG, latLng.toString());
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title(Integer.toString(userID));
		markerOptions.snippet(text);
		return markerOptions;
	}

	@Override
	public void callback(String json) {
		showMarkers(json);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i(TAG, "Clicked: " + marker.getSnippet());
		return false;
	}
}