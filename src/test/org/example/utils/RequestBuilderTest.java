package org.example.utils;

import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RequestBuilderTest {

    HttpUtils httpUtils = HttpUtils.createDefault();


    @Test
    void addHeader() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addHeader("foo", "bar");
        assertTrue(requestBuilder.httpRequest.containsHeader("foo"));
    }

    @Test
    void delHeaders() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addHeader("foo", "bar");
        requestBuilder.delHeaders("foo");
        assertFalse(requestBuilder.httpRequest.containsHeader("foo"));
    }

    @Test
    void addPlainWithContent() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        requestBuilder.addPlain("foobar");
        assertEquals("foobar", requestBuilder.stringContent);
        assertEquals("text/plain", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addPlainWithoutContent() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addPlain(null));
        assertNull(requestBuilder.stringContent);
        assertNull(requestBuilder.contentType);
    }

    @Test
    void addJsonWithContent() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        requestBuilder.addJson("{}");
        assertEquals("{}", requestBuilder.stringContent);
        assertEquals("application/json", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addJsonWithoutContent() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addJson(null));
        assertNull(requestBuilder.stringContent);
        assertNull(requestBuilder.contentType);
    }

    @Test
    void addFormWithContent() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        requestBuilder.addForm("foo", "bar");
        assertTrue(requestBuilder.formContent.contains(new BasicNameValuePair("foo", "bar")));
        assertEquals("application/x-www-form-urlencoded", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addFormWithoutContent() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addForm(null, "bar"));
        assertThrows(Exception.class, ()->requestBuilder.addForm("foo", null));
        assertThrows(Exception.class, ()->requestBuilder.addForm(null, null));
        assertNull(requestBuilder.contentType);
    }

    @Test
    void convertToCharsetWithLegalArgs() {
        assertEquals("GB2312", RequestBuilder.convertToCharset("text/plain; charset=GB2312").displayName());
    }

    @Test
    void convertToCharsetWithIllegalArgs() {
        assertThrows(Exception.class, ()->RequestBuilder.convertToCharset(null));
        assertThrows(Exception.class, ()->RequestBuilder.convertToCharset(""));
    }

    @Test
    void convertToContentTypeWithLegalArgs() {
        assertEquals("text/plain; charset=UTF-8", RequestBuilder.convertToContentType("text/plain; charset=UTF-8").toString());
    }

    @Test
    void convertToContentTypeWithIllegalArgs() {
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType(null));
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType(""));
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType("-xxx"));
    }

    @Test
    void addFormData1() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addFormData("foo", "bar");
        assertEquals("multipart/form-data", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addFormData2() throws IOException {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addFormData("foo", File.createTempFile("test", ".tmp"));
        assertEquals("multipart/form-data", requestBuilder.contentType.getMimeType());
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
    void setEntityForGET() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", url);
        requestBuilder.setEntity();
        assertNull(requestBuilder.httpEntity);
    }

    @Test
    void setPlainEntityForPOST() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addPlain("foobar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);
    }

    @Test
    void setJsonEntityForPOST() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addJson("{}");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);
    }

    @Test
    void setFormEntityForPOST() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addForm("foo", "bar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);
    }

    @Test
    void setFormDataEntityForPOST() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addFormData("foo", "bar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);
    }

    @Test
    void requestForGET() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", url);
        requestBuilder.request();
        assertNotNull(httpUtils.httpResponse);
    }

    @Test
    void requestForPOST() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addPlain("foobar");
        requestBuilder.request();
        assertNotNull(httpUtils.httpResponse);
    }
}