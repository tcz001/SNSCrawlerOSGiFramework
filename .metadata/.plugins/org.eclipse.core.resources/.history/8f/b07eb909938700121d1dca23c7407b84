package crawler.api.service.impl;

import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.fetch.FetchService;

public class WeiboFriendsIDsFetchServiceImpl implements FetchService {
	private static final String PROTECTED_RESOURCE_URL = "https://open.t.qq.com/api/friends/fanslist";
	private static final String apiKey = "801320236";
	private static final String apiSecret = "8e552278a16399189233b111ea11ecf7";
	JSONObject json;
	OAuthimpl oAuthimpl;

	@Override
	public void init() {
		oAuthimpl = new OAuthimpl();
		oAuthimpl.getToken();
	}

	@Override
	public void fetch() {
//		String url = PROTECTED_RESOURCE_URL + "?=&access_token=" + oAuthimpl.accessToken + "&oauth_consumer_key=" + apiKey +"&openid="+ oAuthimpl.client_id + "&oauth_version=2.a"; 
		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL );
		oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("oauth_consumer_key", apiKey);
		request.addQuerystringParameter("openid", oAuthimpl.client_id);
		request.addQuerystringParameter("oauth_version", "2.a");
		System.out.println(request.getCompleteUrl());
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getCode());
		System.out.println(response.getBody());
		json= JSONObject.fromObject(response.getBody());
		System.out.println(json.toString());
		System.out
		.println("Thats it man! Go and build something awesome with Scribe! :)");

	}

	@Override
	public void log() {
	}

}
