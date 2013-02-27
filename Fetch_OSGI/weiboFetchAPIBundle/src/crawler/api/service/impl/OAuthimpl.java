package crawler.api.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import weibo4j.Oauth;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;
import weibo4j.util.BareBonesBrowserLaunch;

public class OAuthimpl {
	private String code;
	private AccessToken accessToken;
	public OAuthimpl(){
		code = "";
		accessToken = null;
	}

	public void askOAuthCode(){
		Oauth oauth = new Oauth();
		try {
			BareBonesBrowserLaunch.openURL(oauth.authorize("code", "", ""));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(oauth.authorize("code", "", ""));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		System.out.print("Hit enter when it's done.[Enter]:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		code = null;
		try {
			code = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void askOAuthToken() {
		accessToken = null;
		Oauth oauth = new Oauth();
		try {
			accessToken = oauth.getAccessTokenByCode(code);
		} catch (WeiboException e) {
			if (401 == e.getStatusCode()) {
			} else {
				e.printStackTrace();
			}
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	
}