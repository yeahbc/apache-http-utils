package org.example.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.Args;


public class ClientBuilder {

    private HttpClientBuilder clientBuilder = HttpClients.custom();
    private CookieStore cookieStore = new BasicCookieStore();

    public ClientBuilder setProxy(String hostname, int port){

        Args.notBlank(hostname, "hostname");
        Args.notNegative(port, "port");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(new HttpHost(hostname, port));
        clientBuilder.setRoutePlanner(routePlanner);
        return this;
    }

    public ClientBuilder setCookie(String domain, String path, String key, String value){

        Args.notBlank(domain, "domain");
        Args.notBlank(path, "path");
        Args.notBlank(key, "key");
        Args.notNull(value, "value");
        BasicClientCookie cookie = new BasicClientCookie(key, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookieStore.addCookie(cookie);
        clientBuilder.setDefaultCookieStore(cookieStore);
        return this;
    }

    public ClientBuilder disableCookie(){
        clientBuilder.disableCookieManagement();
        return this;
    }

    public ClientBuilder disableRedirect(){
        clientBuilder.disableRedirectHandling();
        return this;
    }

    public HttpUtils build(){
        return new HttpUtils(clientBuilder.build());
    }
}
