package org.example.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientBuilderTest {

    @Test
    void setProxy() {
        assertNotNull(new ClientBuilder().setProxy("127.0.0.1", 8888).build());
        assertNotNull(new ClientBuilder().setProxy("127.0.0.1", -1).build());
        assertNotNull(new ClientBuilder().setProxy("www.baidu.com", 8888).build());
    }

    @Test
    void setProxyWithErrors(){
        assertThrows(Exception.class, ()->new ClientBuilder().setProxy("", 8888));
        assertThrows(Exception.class, ()->new ClientBuilder().setProxy(null, 8888));
        assertThrows(Exception.class, ()->new ClientBuilder().setProxy("127.0.0.1", -2));
        assertThrows(Exception.class, ()->new ClientBuilder().setProxy("127.0.0.1", 65536));
    }

    @Test
    void setCookie() {
        assertNotNull(new ClientBuilder().setCookie(".example.com", "/", "foo", "bar").build());
    }

    @Test
    void setCookieWithErrors(){
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(null, "/", "foo", "bar"));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie("", "/", "foo", "bar"));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(".example.com", null, "foo", "bar"));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(".example.com", "", "foo", "bar"));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(".example.com", "", null, "bar"));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(".example.com", "", "", "bar"));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(".example.com", "", "foo", null));
        assertThrows(Exception.class, ()->new ClientBuilder().setCookie(".example.com", "", "foo", ""));
    }

    @Test
    void disableCookie() {
    }

    @Test
    void disableRedirect() {
    }

    @Test
    void build() {
        assertNotNull(new ClientBuilder().defaultBuild());
        assertNotNull(new ClientBuilder()
                .setProxy("127.0.0.1", 8888)
                .setCookie(".example.com", "/", "foo", "bar")
                .build()
        );
    }
}