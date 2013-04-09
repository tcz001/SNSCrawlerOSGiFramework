package crawler.api.service.impl;

import crawler.api.service.TencentWeiboFetchService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import redis.clients.jedis.Jedis;

import java.util.HashSet;

public class TencentWeiboFriendsIDsFetchServiceImpl implements TencentWeiboFetchService, Runnable {
    private static final String GET_FRIENDS_IDs_URL = "https://open.t.qq.com/api/friends/idollist_name";
    private static final String GET_FOLLOWERS_IDs_URL = "https://open.t.qq.com/api/friends/user_fanslist";
    private static final String GET_STATUS_BY_ID_URL = "https://open.t.qq.com/api/statuses/user_timeline";
    JSONObject json;
    TencentOAuthImpl tencentOAuthImpl;
    Jedis jedis;
    Response response;
    OAuthRequest request;
    HashSet<String> accessedUidSet = new HashSet<String>();

    volatile boolean stop = false;

    @Override
    public void init() {
        stop = false;
        jedis = new Jedis("localhost");
        tencentOAuthImpl = new TencentOAuthImpl();
        if (jedis.get("tencent:Token:token") != null)
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
        System.out.println("Now we're going to access tencent...");
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
        JSONArray fdatas = new JSONArray();
        getJsonArray("0", id, fdatas);
        jedis.hset("tencent:uid:" + id, "followers_ids", fdatas.toString());
        return fdatas;
    }

    private void getJsonArray(String startindex, String id, JSONArray fdatas) {
        request = new OAuthRequest(Verb.GET,
                GET_FOLLOWERS_IDs_URL);
        tencentOAuthImpl.service.signRequest(tencentOAuthImpl.accessToken, request);
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("startindex", startindex);
        request.addQuerystringParameter("fopenid", id);
        request.addQuerystringParameter("oauth_consumer_key", tencentOAuthImpl.apiKey);
        request.addQuerystringParameter("openid", tencentOAuthImpl.client_id);
        request.addQuerystringParameter("oauth_version", "2.a");
        request.getCompleteUrl();
        response = request.send();
        json = JSONObject.fromObject(response.getBody());
        fdatas.addAll(json.getJSONObject("data").getJSONArray("info"));
        if (json.getJSONObject("data").get("hasnext").toString().equals("0"))
            getJsonArray(json.getJSONObject("data").get("nextstartpos").toString(), id, fdatas);
    }

    private void fetch_timeline_by_fdata(JSONObject fdata) {
        if (stop) return;
        String fid = fdata.get("openid").toString();
        if (accessedUidSet.contains(fid))
            return;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        accessedUidSet.add(fid);
        jedis.hset("tencent:uid:" + fid, "time_line", json.toString());
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
        stop = false;
        fetch();
        System.out.println("Tencent exiting under request...");
    }
}