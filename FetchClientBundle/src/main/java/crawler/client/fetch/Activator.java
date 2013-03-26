package crawler.client.fetch;

import crawler.api.service.SinaWeiboFetchService;
import crawler.api.service.TencentWeiboFetchService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-3-22
 * Time: 下午4:14
 */
public class Activator implements BundleActivator {

    private ServiceTracker<?, ?> crawlerServiceTracker;
    private SinaWeiboFetchService sinaWeiboFetchService;
    private TencentWeiboFetchService tencentWeiboFetchService;

    public void start(BundleContext context) throws Exception {
        // create a tracker and track the log service
        crawlerServiceTracker =
                new ServiceTracker<Object, Object>(context, SinaWeiboFetchService.class.getName(), null);
        crawlerServiceTracker.open();

        // grab the service
        sinaWeiboFetchService = (SinaWeiboFetchService) crawlerServiceTracker.getService();

        if (sinaWeiboFetchService != null) {
            Thread sina = new Thread(sinaWeiboFetchService);
            sina.start();
        }

        // create a tracker and track the log service
        crawlerServiceTracker =
                new ServiceTracker<Object, Object>(context, TencentWeiboFetchService.class.getName(), null);
        crawlerServiceTracker.open();

        // grab the service
        tencentWeiboFetchService = (TencentWeiboFetchService) crawlerServiceTracker.getService();
        if (tencentWeiboFetchService != null) {
            Thread tencent = new Thread(tencentWeiboFetchService);
            tencent.start();
        }
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if (sinaWeiboFetchService != null)
            sinaWeiboFetchService.log();

        // close the service tracker
        crawlerServiceTracker.close();
        crawlerServiceTracker = null;

        sinaWeiboFetchService = null;
    }
}
