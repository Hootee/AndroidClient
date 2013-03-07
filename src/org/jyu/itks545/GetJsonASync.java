package org.jyu.itks545;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

class GetJsonASync extends AsyncTask<Void, Void, HttpResponse> {
	private final AsyncCallback asyncCallback;
	private final String url;

	public GetJsonASync(AsyncCallback asyncCallback, String url) {
		this.asyncCallback = asyncCallback;
		this.url = url;
	}
	@Override
	protected HttpResponse doInBackground(Void... params) {
		HttpGet request = new HttpGet(url);
//		HttpPost request = new HttpPost(url);
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		try {
			return client.execute(request);
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

				if (asyncCallback != null) {
					asyncCallback.callback(sb.toString());
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
