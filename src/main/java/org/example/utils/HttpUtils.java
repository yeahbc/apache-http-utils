package org.example.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpUtils {

    private CloseableHttpClient httpClient;
    private CloseableHttpResponse httpResponse;

    public HttpUtils(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    public static HttpUtils createDefault(){
        return new HttpUtils(HttpClients.createDefault());
    }

    public static HttpClientBuilder createCustom(){
        return new HttpClientBuilder();
    }

    public HttpRequestBuilder GET(String url){
        return new HttpRequestBuilder(this, "GET", url);
    }

    public HttpRequestBuilder POST(String url){
        return new HttpRequestBuilder(this, "POST", url);
    }

    protected CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    protected void setHttpResponse(CloseableHttpResponse httpResponse){
        this.httpResponse = httpResponse;
    }




    public static void main(String... args){

        HttpUtils utils = HttpUtils.createDefault();

        utils.GET("https://www.baidu.com")
                .addHeader("key", "value")
                .request();

        utils.POST("http://example.org")
                .addHeader("key", "value")
                .addText("hello")
                .request();


//        utils.consume();
//        utils.getContent();
//        utils.getFile();
//
//        utils.close();

    }
}
