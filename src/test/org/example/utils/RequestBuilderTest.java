package org.example.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestBuilderTest {

    HttpUtils utils = HttpUtils.createDefault();
    RequestBuilder request = new RequestBuilder(utils, "GET", "http://example.com");
    
    @Test
    void addHeader() {

        request.addHeader("foo", "bar");
        assertTrue(request.getRequest().containsHeader("foo"));
    }

    @Test
    void addPlain() {
        request.addPlain("foobar");
    }

    @Test
    void addJson() {
    }

    @Test
    void addForm() {
    }

    @Test
    void convertToCharset() {
        assertEquals("GB2312", RequestBuilder.convertToCharset("text/plain; charset=GB2312").name());
    }

    @Test
    void convertToContentType() {
        System.out.println(RequestBuilder.convertToContentType("text/plain; charset=GB2312").toString());
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