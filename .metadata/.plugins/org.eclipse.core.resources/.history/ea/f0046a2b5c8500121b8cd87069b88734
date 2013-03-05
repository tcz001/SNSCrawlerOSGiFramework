package crawler.api.service.impl;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import crawler.api.service.fetch.FetchService;

public class WeiboFriendsIDsFetchServiceImpl implements FetchService {
	private static final String PROTECTED_RESOURCE_URL = "https://api.weibo.com/2/friendships/friends/ids.json";

	OAuthimpl oAuthimpl;

	@Override
	public void init() {
		oAuthimpl = new OAuthimpl();
		oAuthimpl.getToken();
	}

	@Override
	public void fetch() {
		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL);
		oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getCode());
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("Thats it man! Go and build something awesome with Scribe! :)");

	}

	@Override
	public void log() {
	}

}
