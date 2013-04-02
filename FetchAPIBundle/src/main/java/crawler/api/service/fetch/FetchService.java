package crawler.api.service.fetch;

public interface FetchService extends Runnable{
	public void init();

	public void fetch();

	public void log();

    void stop();
}
