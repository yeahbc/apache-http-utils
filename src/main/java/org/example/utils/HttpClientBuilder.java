package org.example.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;


public class HttpClientBuilder {

    private org.apache.http.impl.client.HttpClientBuilder clientBuilder = HttpClients.custom();
    private CookieStore cookieStore = new BasicCookieStore();

    public HttpClientBuilder setProxy(String hostname, int port){
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(new HttpHost(hostname, port));
        clientBuilder.setRoutePlanner(routePlanner);
        return this;
    }

    public HttpClientBuilder setCookie(String domain, String path, String key, String value){

        BasicClientCookie cookie = new BasicClientCookie(key, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookieStore.addCookie(cookie);
        clientBuilder.setDefaultCookieStore(cookieStore);
        return this;
    }

    public HttpClientBuilder disableCookie(){
        clientBuilder.disableCookieManagement();
        return this;
    }

    public HttpClientBuilder disableRedirect(){
        clientBuilder.disableRedirectHandling();
        return this;
    }


    public HttpUtils build(){
        return new HttpUtils(clientBuilder.build());
    }
}
