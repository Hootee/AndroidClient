package org.jyu.itks545;

/**
 * Callback interface for asynchronous network ASyncTask.
 * For example: Show all markers when they retrieved from server.
 *
 */
public interface AsyncCallback {
	public void callback(String json);
}
