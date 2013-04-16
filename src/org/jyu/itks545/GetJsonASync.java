package org.jyu.itks545;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Get the response string from the server.
 *
 */
class GetJsonASync extends AsyncTask<Void, Void, HttpResponse> {
	private static final String TAG = GetJsonASync.class.getSimpleName();

	private final AsyncCallback asyncCallback;
	private final String url;
	private final List<NameValuePair> data;
	
	public GetJsonASync(AsyncCallback asyncCallback, String url, List<NameValuePair> data) {
		this.asyncCallback = asyncCallback;
		this.url = url;
		this.data = data;
	}
	@Override
	protected HttpResponse doInBackground(Void... params) {
		Log.i(TAG, url);
		HttpPost httpPost = new HttpPost(url);
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		StringEntity tmp = null;        

//		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
        	if (data != null) {
        		tmp = new UrlEncodedFormEntity(data);
        		httpPost.setEntity(tmp);
        	}
        } catch (UnsupportedEncodingException e) {
        	Log.e(TAG, "HttpUtils : UnsupportedEncodingException : " + e);
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

	@Override
	protected void onPostExecute(HttpResponse result) {
		if (result != null) {
			Log.i("Content lenght",Long.toString(result.getEntity().getContentLength()));
			try {
				InputStream is = result.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
				is.close();

				Log.i("Content", sb.toString());
				
				if (asyncCallback != null) {
					asyncCallback.callback(sb.toString());
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
