package crawler.api.service.fetch;

import java.util.ArrayList;

public interface FetchService extends Runnable{
	public void init();

	public void fetch();

	public void log(String uid,ArrayList<Weibo> weibo);

    void stop();
}
