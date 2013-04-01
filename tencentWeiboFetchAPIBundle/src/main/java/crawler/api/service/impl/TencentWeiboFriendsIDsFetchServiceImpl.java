package crawler.api.service.impl;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.TencentWeiboFetchService;
import redis.clients.jedis.*;

public class TencentWeiboFriendsIDsFetchServiceImpl implements TencentWeiboFetchService, Runnable {
    private static final String GET_FRIENDS_IDs_URL = "https://open.t.qq.com/api/friends/idollist_name";
    private static final String GET_FOLLOWERS_IDs_URL = "https://open.t.qq.com/api/friends/fanslist_s";
    private static final String GET_STATUS_BY_ID_URL = "https://open.t.qq.com/api/statuses/user_timeline";
    JSONObject json;
    TencentOAuthImpl tencentOAuthImpl;
    Jedis jedis;
    Response response;
    OAuthRequest request;

    volatile boolean stop = false;

    @Override
    public void init() {
        stop = false;
        jedis = new Jedis("localhost");
        tencentOAuthImpl = new TencentOAuthImpl();
        if (jedis.get("tencent:Token:token")!=null)
            tencentOAuthImpl.accessToken = new Token(jedis.get("tencent:Token:token"), jedis.get("tencent:Token:secret"));
        else {
            tencentOAuthImpl.fetchToken();
            Token token = tencentOAuthImpl.getToken();
            jedis.set("tencent:Token:token", token.getToken());
            jedis.set("tencent:Token:secret", token.getSecret());
        }
    }

    @Override
    public void fetch() {
        for (Object data : fetch_datas()) {
            if (stop) return;
            for (Object fdata : fetch_fdatas_by_data((JSONObject) data)) {
                if (stop) return;
                fetch_timeline_by_fdata((JSONObject) fdata);
            }
        }

    }

    private JSONArray fetch_datas() {
        System.out.println("Now we're going to access a protected resource...");
        request = new OAuthRequest(Verb.GET,
                GET_FRIENDS_IDs_URL);
        tencentOAuthImpl.service.signRequest(tencentOAuthImpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("oauth_consumer_key", tencentOAuthImpl.apiKey);
        request.addQuerystringParameter("openid", tencentOAuthImpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        request.getCompleteUrl();
        response = request.send();
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
        request = new OAuthRequest(Verb.GET,
                GET_FOLLOWERS_IDs_URL);
        tencentOAuthImpl.service.signRequest(tencentOAuthImpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("fopenid", id);
        request.addQuerystringParameter("oauth_consumer_key", tencentOAuthImpl.apiKey);
        request.addQuerystringParameter("openid", tencentOAuthImpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        request.getCompleteUrl();
        response = request.send();
        json = JSONObject.fromObject(response.getBody());
        return json.getJSONObject("data").getJSONArray("info");
    }

    private void fetch_timeline_by_fdata(JSONObject fdata) {
        if (stop) return;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String fid = fdata.get("openid").toString();
        System.out.print("Now crawler at the id : ");
        System.out.println(fid);
        request = new OAuthRequest(Verb.GET,
                GET_STATUS_BY_ID_URL);
        tencentOAuthImpl.service.signRequest(tencentOAuthImpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("fopenid", fid);
        request.addQuerystringParameter("oauth_consumer_key", tencentOAuthImpl.apiKey);
        request.addQuerystringParameter("openid", tencentOAuthImpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        request.getCompleteUrl();
        response = request.send();
        json = JSONObject.fromObject(response.getBody());
        jedis.hset("uid:" + fid, "time_line", json.toString());
    }

    @Override
    public void log() {
    }

    @Override
    public void stop() {
        stop = true;
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
        System.out.println("Tencent exiting under request...");
    }
}