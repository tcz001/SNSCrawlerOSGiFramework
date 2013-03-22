package crawler.client.fetch;

import crawler.api.service.SinaWeiboFetchService;
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
    private SinaWeiboFetchService crawlerService;

    public void start(BundleContext context) throws Exception {
    // create a tracker and track the log service
    crawlerServiceTracker =
            new ServiceTracker<Object, Object>(context,  SinaWeiboFetchService.class.getName(), null);
    crawlerServiceTracker.open();

    // grab the service
    crawlerService = (SinaWeiboFetchService) crawlerServiceTracker.getService();

    if(crawlerService != null) {
        crawlerService.fetch();
    }
}

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
            if(crawlerService != null)
                crawlerService.log();

            // close the service tracker
            crawlerServiceTracker.close();
            crawlerServiceTracker = null;

            crawlerService = null;
    }
}
