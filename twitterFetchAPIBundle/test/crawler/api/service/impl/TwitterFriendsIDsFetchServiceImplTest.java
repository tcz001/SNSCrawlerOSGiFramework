package crawler.api.service.impl;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-4-2
 * Time: 上午10:42
 */
public class TwitterFriendsIDsFetchServiceImplTest {
    TwitterFriendsIDsFetchServiceImpl twitterFriendsIDsFetchService;
    Thread twitter;

    TwitterFriendsIDsFetchServiceImplTest() {
        twitterFriendsIDsFetchService = new TwitterFriendsIDsFetchServiceImpl();
        twitter = new Thread(twitterFriendsIDsFetchService);
    }

    public void testInit() {
        twitterFriendsIDsFetchService.init();
    }

    public void testStop() {
        twitterFriendsIDsFetchService.stop();
    }

    public void testRun() {
        twitter.run();
    }

    public static void main(String args[]) {
        TwitterFriendsIDsFetchServiceImplTest test = new TwitterFriendsIDsFetchServiceImplTest();
        test.testInit();
        test.testRun();
        test.testStop();
    }
}
