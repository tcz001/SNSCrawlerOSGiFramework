package crawler.api.service.impl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-5-8
 * Time: 上午10:14
 */
public class HeXunFriendsIDsFetchServiceImpl {
    HashSet<String> SEED_URLs;
    public HeXunFriendsIDsFetchServiceImpl(){
        SEED_URLs = new HashSet<String>();
    }
    ArrayList<String> getOutAnchorUrls(String currentUrl) {
        ArrayList<String> outAnchorUrls = null;
        try {
            System.out.println("Now at "+currentUrl);
            Connection connect = Jsoup.connect(currentUrl)
                    .header("Host", "t.hexun.com")
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.63 Safari/537.31")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "zh-cn,zh;q=0.8")
                    .header("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3")
                    .header("Connection", "keep-alive")
                    .header("Accept-Encoding","gzip,deflate,sdch");
            Document document = connect.get();

            Elements anchors = document.select("a");
            outAnchorUrls = new ArrayList<String>();
            for (Element anchor : anchors) {
                String outUrl = anchor.attr("href");
                if (outUrl.matches("(http://t\\.hexun\\.com)?/g/\\d+_1(\\.html)?")){
                    System.out.println(anchor.attr("href"));
                    outAnchorUrls.add(anchor.attr("href"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outAnchorUrls;
    }
    public void addSEED_URLs(String url){
        this.SEED_URLs.add(url);
    }
    public void fetch_URLs(){
        for(String url : SEED_URLs){
            getOutAnchorUrls(url);
        }
    }
    public static void main(String args[]){
        String stackNum="600000";
        HeXunFriendsIDsFetchServiceImpl heXunFriendsIDsFetchService= new HeXunFriendsIDsFetchServiceImpl();
        heXunFriendsIDsFetchService.addSEED_URLs("http://t.hexun.com/g/" + stackNum + "_1.html");
        heXunFriendsIDsFetchService.fetch_URLs();
    }
}
