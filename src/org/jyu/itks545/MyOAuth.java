package org.jyu.itks545;


/**
 * Base for Twitter OAuth http-messages
 * 
 * @author Bela Borbely <bela.z.borbely at gmail.com>
 * @version 14.2.2013
 */
public class MyOAuth {
	@SuppressWarnings("unused")
	private static final String TAG = MyOAuth.class.getSimpleName();

	private static final String API_URL = "http://itks545.it.jyu.fi/toarjusa/itks545/index.php";
//	private static final String API_URL = "http://192.168.100.41/itks545/index.php";
	private static final String REQUEST_TOKEN_PATH = API_URL + "/request_token";
	private static final String ACCESS_TOKEN_PATH = API_URL + "/access_token";
	private static final String ADD_MESSAGE_PATH = API_URL + "/messages/add";
	private static final String DELETE_MESSAGE_PATH = API_URL + "/messages/delete";

	public static class RequestToken extends OAuthGetRequest {

		public RequestToken(String consumerKey, String consumerSecret) {
			super(consumerKey, consumerSecret, REQUEST_TOKEN_PATH);
		}
	}

	public static class AccessToken extends OAuthGetRequest {

		public AccessToken(String consumerKey, String consumerSecret, String pin, RequestToken requestToken) {
			super(consumerKey, consumerSecret, ACCESS_TOKEN_PATH);
			super.putValue(ParameterKey.oauth_verifier, pin);
			super.putValue(ParameterKey.oauth_token, requestToken.response_oauth_token());
			super.putValue(ParameterKey.oauth_token_secret, requestToken.getResponseString(ParameterKey.oauth_token_secret.toString()));
		}
	}

	public static class AddMessage extends OAuthGetRequest {
		public AddMessage(int userID, double latitude, double longitude, String message, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
			super(consumerKey, consumerSecret, ADD_MESSAGE_PATH + "/" + userID + "/" + latitude + "/" + longitude + "/" + message);
			super.putValue(ParameterKey.oauth_token, accessToken);
            super.putValue(ParameterKey.oauth_token_secret, accessTokenSecret);
		}
	}

	public static class DeleteMessage extends OAuthGetRequest {
		public DeleteMessage(int messageID, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
			super(consumerKey, consumerSecret, DELETE_MESSAGE_PATH + "/" + messageID);
			super.putValue(ParameterKey.oauth_token, accessToken);
            super.putValue(ParameterKey.oauth_token_secret, accessTokenSecret);
		}
	}
}

