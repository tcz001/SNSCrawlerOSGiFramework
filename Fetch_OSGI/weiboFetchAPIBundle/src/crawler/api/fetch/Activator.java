package crawler.api.fetch;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import crawler.api.service.impl.WeiboUserTimelineFetchServiceImpl;
import crawler.api.service.user.FetchService;


public class Activator implements BundleActivator {

	private ServiceTracker<?, ?> crawlerServiceTracker;
	private FetchService crawlerService;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// register the service
		context.registerService(
				FetchService.class.getName(), 
				new WeiboUserTimelineFetchServiceImpl(),
				new Hashtable<String, Object>());
		
		// create a tracker and track the log service
		crawlerServiceTracker = 
			new ServiceTracker<Object, Object>(context, FetchService.class.getName(), null);
		crawlerServiceTracker.open();
		
		// grab the service
		crawlerService = (FetchService) crawlerServiceTracker.getService();

		if(crawlerService != null) {
			crawlerService.fetch();
			crawlerService.log();
			crawlerService.fetch();
			crawlerService.log();
			crawlerService.fetch();
			crawlerService.log();
			crawlerService.fetch();
			crawlerService.log();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if(crawlerService != null)
			crawlerService.log();
		
		// close the service tracker
		crawlerServiceTracker.close();
		crawlerServiceTracker = null;
		
		crawlerService = null;
	}
}
