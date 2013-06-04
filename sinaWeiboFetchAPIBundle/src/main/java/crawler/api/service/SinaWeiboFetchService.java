package crawler.api.service;

import crawler.api.service.fetch.FetchService;
import crawler.api.service.fetch.Weibo;
import net.sf.json.JSONArray;

import java.util.ArrayList;

public interface SinaWeiboFetchService extends FetchService{
    JSONArray fetch_ids();
    JSONArray fetch_fids_by_id(Object id);
    ArrayList<Weibo> fetch_timeline_by_fid(Object fid);
}