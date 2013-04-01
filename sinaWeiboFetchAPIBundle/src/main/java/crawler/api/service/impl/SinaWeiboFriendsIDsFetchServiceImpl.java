package crawler.api.service.impl;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.SinaWeiboFetchService;
import redis.clients.jedis.*;

public class SinaWeiboFriendsIDsFetchServiceImpl implements SinaWeiboFetchService, Runnable {
    private static final String GET_FRIENDS_IDs_URL = "https://api.weibo.com/2/friendships/friends/ids.json";
    private static final String GET_FOLLOWERS_IDs_URL = "https://api.weibo.com/2/friendships/followers/ids.json";
    private static final String GET_STATUS_BY_ID_URL = "https://api.weibo.com/2/statuses/user_timeline.json";
    JSONObject json;
    SinaOAuthImpl sinaOAuthImpl;
    Jedis jedis;
    Response response;
    OAuthRequest request;

    volatile boolean stop = false;

    @Override
    public void init() {
        stop = false;
        jedis = new Jedis("localhost");
        sinaOAuthImpl = new SinaOAuthImpl();
        if (jedis.get("sina:Token:token")!=null)
            sinaOAuthImpl.accessToken = new Token(jedis.get("sina:Token:token"), jedis.get("sina:Token:secret"));
        else {
            sinaOAuthImpl.fetchToken();
            Token token = sinaOAuthImpl.getToken();
            jedis.set("sina:Token:token", token.getToken());
            jedis.set("sina:Token:secret", token.getSecret());
        }
    }

    @Override
    public void fetch() {
        for (Object id : fetch_ids()) {
            if (stop) return;
            for (Object follower_id : fetch_fids_by_id(id)) {
                if (stop) return;
                fetch_timeline_by_fid(follower_id);
            }
        }
    }

    private JSONArray fetch_ids() {
        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a friends Idlist...");
        request = new OAuthRequest(Verb.GET,
                GET_FRIENDS_IDs_URL);
        sinaOAuthImpl.service.signRequest(sinaOAuthImpl.accessToken, request);
        response = request.send();
        System.out.println("Got it! Lets see what we found...");
        json = JSONObject.fromObject(response.getBody());
        return json.getJSONArray("ids");
    }

    private JSONArray fetch_fids_by_id(Object id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.print("Now crawler at the id : ");
        System.out.println(id.toString());
        request = new OAuthRequest(Verb.GET,
                GET_FOLLOWERS_IDs_URL);
        sinaOAuthImpl.service.signRequest(sinaOAuthImpl.accessToken, request);
        request.addQuerystringParameter("uid", id.toString());
        response = request.send();
        json = JSONObject.fromObject(response.getBody());
        JSONArray ids = json.getJSONArray("ids");
        jedis.hset("uid:" + id.toString(), "followers_ids", ids.toString());
        return ids;
    }

    private void fetch_timeline_by_fid(Object fid) {
        if (stop) return;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.print("Now crawler at the id : ");
        System.out.println(fid.toString());
        try {
            request = new OAuthRequest(Verb.GET,
                    GET_STATUS_BY_ID_URL);
            sinaOAuthImpl.service.signRequest(sinaOAuthImpl.accessToken, request);
            request.addQuerystringParameter("uid", fid.toString());
            response = request.send();
            if (response.getCode() == 200) {
                json = JSONObject.fromObject(response.getBody());
                jedis.hset("uid:" + fid.toString(), "time_line", json.toString());
            } else fetch_timeline_by_fid(fid);
        } catch (OAuthConnectionException o) {
            fetch_timeline_by_fid(fid);
        }
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
        System.out.println("Sina exiting under request...");
    }
}