package org.example.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestBuilderTest {

    HttpUtils utils = HttpUtils.createDefault();
    RequestBuilder request = new RequestBuilder(utils, "GET", "http://example.com");
    
    @Test
    void addHeader() {

        request.addHeader("key", "value");
        assertTrue(request.getRequest().containsHeader("key"));
    }

    @Test
    void addPlain() {
    }

    @Test
    void addJson() {
    }

    @Test
    void addForm() {
    }

    @Test
    void convertToCharset() {
    }

    @Test
    void convertToContentType() {
    }

    @Test
    void addFormData() {
    }

    @Test
    void testAddFormData() {
    }

    @Test
    void testAddFormData1() {
    }

    @Test
    void testAddFormData2() {
    }

    @Test
    void testAddFormData3() {
    }

    @Test
    void request() {
    }
}