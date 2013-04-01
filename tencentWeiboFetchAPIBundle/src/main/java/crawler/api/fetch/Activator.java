package crawler.api.fetch;

import java.util.Hashtable;

import crawler.api.service.impl.TencentWeiboFriendsIDsFetchServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import crawler.api.service.TencentWeiboFetchService;


public class Activator implements BundleActivator {

    private ServiceTracker<?, ?> crawlerServiceTracker;
    private TencentWeiboFetchService crawlerService;
    Thread tencent;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        // register the service
        context.registerService(
                TencentWeiboFetchService.class.getName(),
                new TencentWeiboFriendsIDsFetchServiceImpl(),
                new Hashtable<String, Object>());

        // create a tracker and track the log service
        crawlerServiceTracker =
                new ServiceTracker<Object, Object>(context, TencentWeiboFetchService.class.getName(), null);
        crawlerServiceTracker.open();

        // grab the service
        crawlerService = (TencentWeiboFetchService) crawlerServiceTracker.getService();

        if (crawlerService != null) {
            crawlerService.init();
            tencent = new Thread(crawlerService);
            tencent.start();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        if (crawlerService != null)
            crawlerService.stop();

        // close the service tracker
        crawlerServiceTracker.close();
        crawlerServiceTracker = null;

        crawlerService = null;
    }
}