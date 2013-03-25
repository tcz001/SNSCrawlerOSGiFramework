package crawler.api.service.impl;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.SinaWeiboFetchService;
import redis.clients.jedis.*;

public class sinaWeiboFriendsIDsFetchServiceImpl implements SinaWeiboFetchService,Runnable {
    private static final String GET_FRIENDS_IDs_URL = "https://api.weibo.com/2/friendships/friends/ids.json";
    private static final String GET_FOLLOWERS_IDs_URL = "https://api.weibo.com/2/friendships/followers/ids.json";
    private static final String GET_STATUS_BY_ID_URL = "https://api.weibo.com/2/statuses/user_timeline.json";
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
        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a friends Idlist...");
        request = new OAuthRequest(Verb.GET,
                GET_FRIENDS_IDs_URL);
        oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
        response = request.send();
        request = null;
        System.out.println("Got it! Lets see what we found...");
        json = JSONObject.fromObject(response.getBody());
        response = null;
        for (Object id : json.getJSONArray("ids")) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.print("Now crawler at the id : ");
            System.out.println(id.toString());
            System.out.println("------------Getting--Follows--IDs---------------");
            request = new OAuthRequest(Verb.GET,
                    GET_FOLLOWERS_IDs_URL);
            oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
            request.addQuerystringParameter("uid", id.toString());
            response = request.send();
            request = null;
            System.out.println(response.getCode());
            json = JSONObject.fromObject(response.getBody());
            response = null;
            jedis.hset("uid:" + id.toString(), "followers_ids", json.getJSONArray("ids").toString());
            for (Object follower_id : json.getJSONArray("ids")) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.print("Now crawler at the id : ");
                System.out.println(follower_id.toString());
                fetch_timeline_by_id(id, follower_id);
            }
            json = null;
        }
        json = null;
    }

    private void fetch_timeline_by_id(Object uid, Object fid) {
        try {
            request = new OAuthRequest(Verb.GET,
                    GET_STATUS_BY_ID_URL);
            oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
            request.addQuerystringParameter("uid", uid.toString());
            response = request.send();
            request = null;
            if (response.getCode() == 200) {
                json = JSONObject.fromObject(response.getBody());
                jedis.hset("uid:" + fid.toString(), "time_line", json.toString());
                json = null;
                response = null;
                System.gc();
            } else fetch_timeline_by_id(uid, fid);
        } catch (OAuthConnectionException o) {
            fetch_timeline_by_id(uid, fid);
        }
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