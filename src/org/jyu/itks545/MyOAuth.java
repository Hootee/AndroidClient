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

	private static final String TESTAPP_OAUTH_CONSUMERKEY = "54352ee5622f80cdd573277c6158fade051263df6";
	private static final String TESTAPP_OAUTH_CONSUMERSECRET = "2775bbf72e96bf80fdea287326b7634c";
	private static final String TEST_REGISTER_PATH = "http://itks545/demo.php/register";
	private static final String TEST_REQUEST_TOKEN_PATH = "http://itks545/demo.php/request_token";
	private static final String TEST_AUTHORIZE_PATH = "http://itks545/demo.php/authorize";
	private static final String TEST_ACCESS_TOKEN_PATH = "http://itks545/demo.php/access_token";

	public static class RequestToken extends OAuthGetRequest {

		public RequestToken() {
			super(TESTAPP_OAUTH_CONSUMERKEY, TESTAPP_OAUTH_CONSUMERSECRET, TEST_REQUEST_TOKEN_PATH);
		}
	}

	public static class Authorize extends OAuthGetRequest {

		private final RequestToken requestToken;

		public Authorize(RequestToken requestToken) {
			super(TESTAPP_OAUTH_CONSUMERKEY, TESTAPP_OAUTH_CONSUMERSECRET, TEST_AUTHORIZE_PATH);
			this.requestToken = requestToken;
		}

		public URI getURI() throws URISyntaxException {
			return new URI(TEST_AUTHORIZE_PATH + "?oauth_token=" + requestToken.response_oauth_token());
		}
	}

	public static class AccessToken extends OAuthGetRequest {

		public AccessToken(String pin, RequestToken requestToken) {
			super(TESTAPP_OAUTH_CONSUMERKEY, TESTAPP_OAUTH_CONSUMERSECRET, TEST_ACCESS_TOKEN_PATH);
			super.putValue(ParameterKey.oauth_verifier, pin);
			super.putValue(ParameterKey.oauth_token, requestToken.response_oauth_token());
			super.putValue(ParameterKey.oauth_token_secret, requestToken.getResponseString(ParameterKey.oauth_token_secret.toString()));
		}
	}
}

