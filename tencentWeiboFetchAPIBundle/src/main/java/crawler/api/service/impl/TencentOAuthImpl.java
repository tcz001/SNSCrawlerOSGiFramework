package crawler.api.service.impl;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.oauth.*;
import org.scribe.model.*;

import crawler.api.service.oauthAccess.OAuthAccessService;

import java.util.*;

public class TencentOAuthImpl implements OAuthAccessService {
	private static final String NETWORK_NAME = "TencentWeibo";
	private static final Token EMPTY_TOKEN = null;

	String apiKey;
	String apiSecret;
	OAuthService service;
	Token accessToken;
	String client_id;
	private Scanner in;

	public TencentOAuthImpl() {
		// Replace these with your own api key and secret
		apiKey = "801320236";
		apiSecret = "8e552278a16399189233b111ea11ecf7";
		service = new ServiceBuilder()
				.provider(TencentWeiboApi.class).apiKey(apiKey)
				.apiSecret(apiSecret)
				.callback("https://api.weibo.com/oauth2/default.html").build();
	}

	@Override
	public void fetchToken() {
		in = new Scanner(System.in);

		System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
		System.out.println();

		// Obtain the Authorization URL
		System.out.println("Fetching the Authorization URL...");
		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
		System.out.println("Got the Authorization URL!");
		System.out.println("Now go and authorize Scribe here:");
		System.out.println(authorizationUrl);
		System.out.println("And paste the authorization code here");
		System.out.print(">>");
		Verifier verifier = new Verifier(in.nextLine());
		System.out.println("And paste the client_id here");
		System.out.print(">>");
		client_id = new String("B94FB0DE54C3269F73C737311E12B5D5");//in.nextLine());
		System.out.println("client_id = " + client_id);

		// Trade the Request Token and Verifier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: "
				+ accessToken.getToken() + " )");
		System.out.println();
	}

    @Override
    public Token getToken() {
        return this.accessToken;
    }
}