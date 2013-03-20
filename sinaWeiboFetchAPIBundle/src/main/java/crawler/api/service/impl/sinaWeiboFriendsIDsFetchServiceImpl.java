package crawler.api.service.impl;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import net.sf.json.*;

import crawler.api.service.SinaWeiboFetchService;
import redis.clients.jedis.*;

public class sinaWeiboFriendsIDsFetchServiceImpl implements SinaWeiboFetchService {
    private static final String GET_FRIENDS_IDs_URL = "https://api.weibo.com/2/friendships/friends/ids.json";
    private static final String GET_FOLLOWERS_IDs_URL = "https://api.weibo.com/2/friendships/followers/ids.json";
    private static final String GET_STATUS_BY_ID_URL = "https://api.weibo.com/2/statuses/user_timeline.json";
    JSONObject json;
    OAuthimpl oAuthimpl;
    Jedis jedis;


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
        OAuthRequest request = new OAuthRequest(Verb.GET,
                GET_FRIENDS_IDs_URL);
        oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());
        json = JSONObject.fromObject(response.getBody());
        for (Object id : json.getJSONArray("ids")) {
            try {
                Thread.sleep(1000);
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
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            json = JSONObject.fromObject(response.getBody());
            jedis.hset("uid:" + id.toString(), "followers_ids", json.getJSONArray("ids").toString());
            for (Object follower_id : json.getJSONArray("ids")) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.print("Now crawler at the id : ");
                System.out.println(id.toString());
                System.out.println("------------Getting--Follows--IDs---------------");
                request = new OAuthRequest(Verb.GET,
                        GET_STATUS_BY_ID_URL);
                oAuthimpl.service.signRequest(oAuthimpl.accessToken, request);
                request.addQuerystringParameter("uid", id.toString());
                response = request.send();
                System.out.println("Got it! Lets see what we found...");
                System.out.println();
                System.out.println(response.getCode());
                json = JSONObject.fromObject(response.getBody());
                jedis.hset("uid:" + follower_id.toString(), "time_line", json.toString());
            }
        }
    }

    @Override
    public void log() {
    }

}