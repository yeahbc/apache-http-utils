package org.example.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpUtilsTest {

    HttpUtils utils = HttpUtils.createDefault();

    @Test
    void createDefault() {
        assertNotNull(HttpUtils.createDefault());
    }

    @Test
    void createCustom() {
        assertNotNull(HttpUtils.createCustom().build());
    }

    @Test
    void GET() {
        utils.GET("http://www.example.com").request();
        assertEquals(200, utils.getStatusCode());
    }

    @Test
    void HEAD() {
    }

    @Test
    void POST() {
    }

    @Test
    void PUT() {
    }

    @Test
    void DELETE() {
    }

    @Test
    void TRACE() {
    }

    @Test
    void OPTIONS() {
    }

    @Test
    void getStatusCode() {
    }

    @Test
    void getHeaders() {
        utils.GET("http://www.example.com").request();
        assertEquals("text/html; charset=UTF-8", utils.getHeaders("Content-Type")[0]);
    }

    @Test
    void consume() {
    }

    @Test
    void getContent() {
    }

    @Test
    void getFile() {
    }

    @Test
    void close() {
    }
}