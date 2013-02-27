package crawler.api.service.impl;

import crawler.api.service.user.FetchService;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class WeiboUserTimelineFetchServiceImpl implements FetchService {

	StatusWapper status;
	OAuthimpl oAuthimpl;
	
	public WeiboUserTimelineFetchServiceImpl() {
		oAuthimpl = new OAuthimpl();
	}

	@Override
	public void fetch() {
		if (oAuthimpl.getAccessToken() == null)
			askOAuthToken();
		Timeline tm = new Timeline();
		tm.client.setToken(oAuthimpl.getAccessToken().getAccessToken());
		try {
			status = tm.getUserTimeline();
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void log() {
		for (Status s : status.getStatuses()) {
			System.out.println(s.toString());
		}
		System.out.println(status.getNextCursor());
		System.out.println(status.getPreviousCursor());
		System.out.println(status.getTotalNumber());
		System.out.println(status.getHasvisible());
	}

	public void askOAuthCode() {
		oAuthimpl.askOAuthCode();
	}

	public void askOAuthToken() {
		this.askOAuthCode();
		oAuthimpl.askOAuthToken();
	}
}
