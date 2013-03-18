package crawler.api.fetch;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import crawler.api.service.TencentWeiboFetchService;

import crawler.api.service.impl.tencentWeiboFriendsIDsFetchServiceImpl;


public class Activator implements BundleActivator {

	private ServiceTracker<?, ?> crawlerServiceTracker;
	private TencentWeiboFetchService crawlerService;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		// register the service
		context.registerService(
				TencentWeiboFetchService.class.getName(), 
				new tencentWeiboFriendsIDsFetchServiceImpl(),
				new Hashtable<String, Object>());
		
		// create a tracker and track the log service
		crawlerServiceTracker = 
			new ServiceTracker<Object, Object>(context, TencentWeiboFetchService.class.getName(), null);
		crawlerServiceTracker.open();
		
		// grab the service
		crawlerService = (TencentWeiboFetchService) crawlerServiceTracker.getService();

		if(crawlerService != null) {
			crawlerService.init();
			crawlerService.fetch();
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