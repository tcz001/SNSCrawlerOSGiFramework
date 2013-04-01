package crawler.api.service.impl;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-4-1
 * Time: 下午12:34
 */
public class TencentWeiboFriendsIDsFetchServiceImplTest {
    TencentWeiboFriendsIDsFetchServiceImpl tencentWeiboFriendsIDsFetchService;
    Thread tencent;

    TencentWeiboFriendsIDsFetchServiceImplTest() {
        tencentWeiboFriendsIDsFetchService = new TencentWeiboFriendsIDsFetchServiceImpl();
        tencent = new Thread(tencentWeiboFriendsIDsFetchService);
    }

    public void testInit() {
        tencentWeiboFriendsIDsFetchService.init();
    }

    public void testStop() {
        tencentWeiboFriendsIDsFetchService.stop();
    }

    public void testRun() {
        tencent.run();
    }

    public static void main(String args[]) {
        TencentWeiboFriendsIDsFetchServiceImplTest test = new TencentWeiboFriendsIDsFetchServiceImplTest();
        test.testInit();
        test.testRun();
        test.testStop();
    }
}
