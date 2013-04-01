package crawler.api.service.oauthAccess;
import org.scribe.model.*;

public interface OAuthAccessService {
	void fetchToken();
    Token getToken();
}
