package crawler.api.service.impl;

import crawler.api.service.TwitterFetchService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import redis.clients.jedis.Jedis;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-4-2
 * Time: 上午9:58
 */
public class TwitterFriendsIDsFetchServiceImpl implements TwitterFetchService,Runnable{

    private static final String GET_FRIENDS_IDs_URL = "https://api.twitter.com/1.1/friends/ids.json";
    private static final String GET_FOLLOWERS_IDs_URL = "https://api.twitter.com/1.1/followers/ids.json";
    private static final String GET_STATUS_BY_ID_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    JSONObject json;
    TwitterOAuthImpl twitterOAuth;
    Jedis jedis;
    Response response;
    OAuthRequest request;

    volatile boolean stop = false;

    @Override
    public void init() {
        stop = false;
        jedis = new Jedis("localhost");
        twitterOAuth = new TwitterOAuthImpl();
        if (jedis.get("twitter:Token:token") != null)
            twitterOAuth.accessToken = new Token(jedis.get("twitter:Token:token"), jedis.get("twitter:Token:secret"));
        else {
            twitterOAuth.fetchToken();
            Token token = twitterOAuth.getToken();
            jedis.set("twitter:Token:token", token.getToken());
            jedis.set("twitter:Token:secret", token.getSecret());
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
        System.out.println("Now we're going to access a protected resource...");
        request = new OAuthRequest(Verb.GET, GET_FRIENDS_IDs_URL);
        twitterOAuth.service.signRequest(twitterOAuth.accessToken, request);
        response = request.send();
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
        request = new OAuthRequest(Verb.GET,GET_FOLLOWERS_IDs_URL);
        request.addQuerystringParameter("user_id", id.toString());
        twitterOAuth.service.signRequest(twitterOAuth.accessToken, request);
        response = request.send();

        json = JSONObject.fromObject(response.getBody());
        return json.getJSONArray("ids");
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
        request = new OAuthRequest(Verb.GET, GET_STATUS_BY_ID_URL);
        twitterOAuth.service.signRequest(twitterOAuth.accessToken, request);
        request.addQuerystringParameter("user_id", fid.toString());
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
        stop = false;
        fetch();
        System.out.println("Twitter exiting under request...");
    }
}
