package crawler.api.service.impl;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.TencentWeiboFetchService;
import redis.clients.jedis.*;

public class tencentWeiboFriendsIDsFetchServiceImpl implements TencentWeiboFetchService, Runnable {
    private static final String GET_FRIENDS_IDs_URL = "https://open.t.qq.com/api/friends/idollist_name";
    private static final String GET_FOLLOWERS_IDs_URL = "https://open.t.qq.com/api/friends/fanslist_s";
    private static final String GET_STATUS_BY_ID_URL = "https://open.t.qq.com/api/statuses/user_timeline";
    JSONObject json;
    OAuthimpl oAuthimpl;
    Jedis jedis;
    Response response;
    OAuthRequest request;

    @Override
    public void init() {
        oAuthimpl = new OAuthimpl();
        oAuthimpl.getToken();
        jedis = new Jedis("localhost");
    }

    @Override
    public void fetch() {
        for (Object data : fetch_datas()) {
            for (Object fdata : fetch_fdatas_by_data((JSONObject) data)){
                fetch_timeline_by_fdata((JSONObject) fdata);
            }
        }

    }

    private JSONArray fetch_datas() {
        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        request = new OAuthRequest(Verb.GET,
                GET_FRIENDS_IDs_URL);
        oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("oauth_consumer_key", oAuthimpl.apiKey);
        request.addQuerystringParameter("openid", oAuthimpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        System.out.println(request.getCompleteUrl());
        response = request.send();
        request = null;
        System.out.println("Got it! Lets see what we found...");
        System.out.println(response.getCode());
        System.out.println(response.getBody());
        json = JSONObject.fromObject(response.getBody());
        return json.getJSONObject("data").getJSONArray("info");
    }

    private JSONArray fetch_fdatas_by_data(JSONObject data) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String id = data.get("openid").toString();
        System.out.print("Now crawler at the id : ");
        System.out.println(id);
        System.out.println("------------Getting--Follows--IDs---------------");
        request = new OAuthRequest(Verb.GET,
                GET_FOLLOWERS_IDs_URL);
        oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("fopenid", id);
        request.addQuerystringParameter("oauth_consumer_key", oAuthimpl.apiKey);
        request.addQuerystringParameter("openid", oAuthimpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        System.out.println(request.getCompleteUrl());
        response = request.send();
        json = JSONObject.fromObject(response.getBody());
        return json.getJSONObject("data").getJSONArray("info");
    }

    private void fetch_timeline_by_fdata(JSONObject fdata) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String fid = fdata.get("openid").toString();
        System.out.print("Now crawler at the id : ");
        System.out.println(fid.toString());
        request = new OAuthRequest(Verb.GET,
                GET_STATUS_BY_ID_URL);
        oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("fopenid", fid);
        request.addQuerystringParameter("oauth_consumer_key", oAuthimpl.apiKey);
        request.addQuerystringParameter("openid", oAuthimpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        System.out.println(request.getCompleteUrl());
        response = request.send();
        json = JSONObject.fromObject(response.getBody());
        jedis.hset("uid:" + fid, "time_line", json.toString());
    }

    @Override
    public void log() {
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        fetch();
    }
}