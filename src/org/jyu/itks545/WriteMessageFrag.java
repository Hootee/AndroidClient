package org.jyu.itks545;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WriteMessageFrag extends Fragment {
	private static final String TAG = WriteMessageFrag.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.activity_writemessage, container, false);
		 
		return myFragmentView;
	}
}
