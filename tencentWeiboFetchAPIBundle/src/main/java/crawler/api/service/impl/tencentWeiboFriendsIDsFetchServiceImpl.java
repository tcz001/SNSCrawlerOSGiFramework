package crawler.api.service.impl;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.TencentWeiboFetchService;

public class tencentWeiboFriendsIDsFetchServiceImpl implements TencentWeiboFetchService {
	private static final String GET_FRIENDS_IDs_URL = "https://open.t.qq.com/api/friends/idollist_name";
	private static final String GET_FOLLOWERS_IDs_URL = "https://open.t.qq.com/api/friends/fanslist_s";
	private static final String apiKey = "801320236";
	JSONObject json;
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
				GET_FRIENDS_IDs_URL );
		oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("oauth_consumer_key", apiKey);
		request.addQuerystringParameter("openid", oAuthimpl.client_id);
		request.addQuerystringParameter("oauth_version", "2.a");
		System.out.println(request.getCompleteUrl());
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println(response.getCode());
		System.out.println(response.getBody());
		json= JSONObject.fromObject(response.getBody());
		for (Object data : json.getJSONObject("data").getJSONArray("info")){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String id = ((JSONObject) data).get("openid").toString();
			System.out.print("Now crawler at the id : ");
			System.out.println(id);
			System.out.println("------------Getting--Follows--IDs---------------");
			request = new OAuthRequest(Verb.GET,
					GET_FRIENDS_IDs_URL );
			oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
			request.addQuerystringParameter("format", "json");
			request.addQuerystringParameter("fopenid", id);
			request.addQuerystringParameter("oauth_consumer_key", apiKey);
			request.addQuerystringParameter("openid", oAuthimpl.client_id);
			request.addQuerystringParameter("oauth_version", "2.a");
			System.out.println(request.getCompleteUrl());
			response = request.send();
			System.out.println("Got it! Lets see what we found...");
			System.out.println(response.getCode());
			System.out.println(response.getBody());
			json= JSONObject.fromObject(response.getBody());
			System.out.println("---------------------------");
		}

	}

	@Override
	public void log() {
	}

}