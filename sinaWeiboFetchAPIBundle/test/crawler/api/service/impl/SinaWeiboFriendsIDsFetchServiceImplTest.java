package crawler.api.service.impl;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-4-1
 * Time: 下午12:09
 */
public class SinaWeiboFriendsIDsFetchServiceImplTest {
    SinaWeiboFriendsIDsFetchServiceImpl sinaWeiboFriendsIDsFetchService;
    Thread sina;

    SinaWeiboFriendsIDsFetchServiceImplTest() {
        sinaWeiboFriendsIDsFetchService = new SinaWeiboFriendsIDsFetchServiceImpl();
        sina = new Thread(sinaWeiboFriendsIDsFetchService);
    }

    public void testInit() {
        sinaWeiboFriendsIDsFetchService.init();
    }

    public void testStop() {
        sinaWeiboFriendsIDsFetchService.stop();
    }

    public void testRun() {
        sina.run();
    }

    public static void main(String args[]) {
        SinaWeiboFriendsIDsFetchServiceImplTest test = new SinaWeiboFriendsIDsFetchServiceImplTest();
        test.testInit();
        test.testRun();
        test.testStop();
    }
}
