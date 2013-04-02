package crawler.api.service.impl;

import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.oauth.*;
import org.scribe.model.*;

import crawler.api.service.oauthAccess.OAuthAccessService;

import java.util.*;

public class TwitterOAuthImpl implements OAuthAccessService {
    private static final String NETWORK_NAME = "Twitter";
    private static final Token EMPTY_TOKEN = null;

    String apiKey;
    String apiSecret;
    OAuthService service;
    Token accessToken;
    private Scanner in;

    public TwitterOAuthImpl() {
        // Replace these with your own api key and secret
        apiKey = "3Yfh78XTUiOhGm3lDOug";
        apiSecret = "Exyna93nwEyk15vPls0b2HZmZSQXtaisCMwkhslNPM0";
        service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback("https://api.weibo.com/oauth2/default.html")
                .build();
    }

    @Override
    public void fetchToken() {
        in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");
        Token request_Token = service.getRequestToken();
        String authorizationUrl = service.getAuthorizationUrl(request_Token);
        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize Scribe here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: "
                + accessToken + " )");
        System.out.println();
    }

    @Override
    public Token getToken() {
        return this.accessToken;
    }
}

