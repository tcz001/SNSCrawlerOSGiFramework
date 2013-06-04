package crawler.api.service.fetch;

/**
 * Created with IntelliJ IDEA.
 * User: tcz
 * Date: 13-6-3
 * Time: 上午9:58
 */
public class Weibo {
    public String id;
    public String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Weibo(String id, String text) {
        this.id = id;
        this.text = text;
    }
}
