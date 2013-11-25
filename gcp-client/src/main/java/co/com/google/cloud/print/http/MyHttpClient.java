package co.com.google.cloud.print.http;

import java.util.Map;

import org.apache.http.HttpResponse;

public interface MyHttpClient {
	public HttpResponse getURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
    public HttpResponse postURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
    public HttpResponse putURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
    public HttpResponse deleteURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
    public String getCookie(String cookieKey,String cookieValue);
    public String getResponseBodyAsString(HttpResponse response) throws Exception;
}