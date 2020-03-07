package com.DavidMuheri.KadaiDen.Helper;


import com.DavidMuheri.KadaiDen.Setting.AppConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerClass {
    private static String baseUrl = AppConfig.SERVER;
	private static AsyncHttpClient client = new AsyncHttpClient();
	public static String getUrl(String subUrl){
		return baseUrl+subUrl;
	}
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		//client.setMaxRetriesAndTimeout(0, 10000);
		client.post(getUrl(url), params, responseHandler);
	}
}
