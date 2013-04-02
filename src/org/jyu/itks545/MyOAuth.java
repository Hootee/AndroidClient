package org.jyu.itks545;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Base for Twitter OAuth http-messages
 * 
 * @author Bela Borbely <bela.z.borbely at gmail.com>
 * @version 14.2.2013
 */
public class MyOAuth {
	@SuppressWarnings("unused")
	private static final String TAG = MyOAuth.class.getSimpleName();

	private static final String CLIENT_IDENTIFIER = "40e627f1f1fd986f232fd27ea98c004d0515ad9f6";
	private static final String CLIENT_SHAREDSECRET = "0d9f59850c80b61dbf26dd930c13ed10";
	private static final String API_URL = "http://itks545.it.jyu.fi/toarjusa/itks545/index.php";
	private static final String REGISTER_PATH = API_URL + "/register";
	private static final String REQUEST_TOKEN_PATH = API_URL + "/request_token";
	private static final String AUTHORIZE_PATH = API_URL + "/authorize";
	private static final String ACCESS_TOKEN_PATH = API_URL + "/access_token";

	public static class RequestToken extends OAuthGetRequest {

		public RequestToken() {
			super(CLIENT_IDENTIFIER, CLIENT_SHAREDSECRET, REQUEST_TOKEN_PATH);
		}
	}

	public static class Authorize extends OAuthGetRequest {

		private final RequestToken requestToken;

		public Authorize(RequestToken requestToken) {
			super(CLIENT_IDENTIFIER, CLIENT_SHAREDSECRET, AUTHORIZE_PATH);
			this.requestToken = requestToken;
		}

		public URI getURI() throws URISyntaxException {
			return new URI(AUTHORIZE_PATH + "?oauth_token=" + requestToken.response_oauth_token());
		}
	}

	public static class AccessToken extends OAuthGetRequest {

		public AccessToken(String pin, RequestToken requestToken) {
			super(CLIENT_IDENTIFIER, CLIENT_SHAREDSECRET, ACCESS_TOKEN_PATH);
			super.putValue(ParameterKey.oauth_verifier, pin);
			super.putValue(ParameterKey.oauth_token, requestToken.response_oauth_token());
			super.putValue(ParameterKey.oauth_token_secret, requestToken.getResponseString(ParameterKey.oauth_token_secret.toString()));
		}
	}
}

