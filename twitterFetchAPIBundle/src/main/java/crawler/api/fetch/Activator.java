package crawler.api.fetch;

import java.util.Hashtable;

import crawler.api.service.impl.TwitterFriendsIDsFetchServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import crawler.api.service.TwitterFetchService;


public class Activator implements BundleActivator {

    private ServiceTracker<?, ?> crawlerServiceTracker;
    private TwitterFetchService crawlerService;
    Thread twitter;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        // register the service
        context.registerService(
                TwitterFetchService.class.getName(),
                new TwitterFriendsIDsFetchServiceImpl(),
                new Hashtable<String, Object>());

        // create a tracker and track the log service
        crawlerServiceTracker =
                new ServiceTracker<Object, Object>(context, TwitterFetchService.class.getName(), null);
        crawlerServiceTracker.open();

        // grab the service
        crawlerService = (TwitterFetchService) crawlerServiceTracker.getService();

        if (crawlerService != null) {
            crawlerService.init();
            twitter = new Thread(crawlerService);
            twitter.start();
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

