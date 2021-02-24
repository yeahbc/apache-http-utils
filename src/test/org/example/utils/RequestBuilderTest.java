package org.example.utils;

import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RequestBuilderTest {

    HttpUtils httpUtils = HttpUtils.createDefault();


    @Test
    void convertToCharset() {
        assertNotNull(RequestBuilder.convertToCharset("text/plain; charset=GB2312"));
        assertNull(RequestBuilder.convertToCharset("text/plain"));
    }

    @Test
    void convertToCharsetWithErrors() {
        assertThrows(Exception.class, ()->RequestBuilder.convertToCharset(null));
        assertThrows(Exception.class, ()->RequestBuilder.convertToCharset(""));
        assertThrows(Exception.class, ()->RequestBuilder.convertToCharset("text/plain; charset=UTF-9"));
    }

    @Test
    void convertToContentType() {
        assertEquals("text/plain; charset=UTF-8", RequestBuilder.convertToContentType("text/plain; charset=UTF-8").toString());
        assertEquals("text/plain", RequestBuilder.convertToContentType("text/plain").toString());
    }

    @Test
    void convertToContentTypeWithErrors() {
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType(null));
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType(""));
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType(","));
        assertThrows(Exception.class, ()->RequestBuilder.convertToContentType("text/plain; charset=UTF-9"));
    }

    @Test
    void addHeader() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addHeader("foo", "bar");
        assertTrue(requestBuilder.httpRequest.containsHeader("foo"));
        requestBuilder.addHeader("foo2", "");
        assertTrue(requestBuilder.httpRequest.containsHeader("foo2"));
    }

    @Test
    void addHeaderWithErrors(){

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addHeader("foo", null));
        assertThrows(Exception.class, ()->requestBuilder.addHeader("", "bar"));
        assertThrows(Exception.class, ()->requestBuilder.addHeader(null, "bar"));
    }

    @Test
    void delHeaders() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addHeader("foo", "bar");
        requestBuilder.delHeaders("foo");
        requestBuilder.delHeaders("foo2");
        assertFalse(requestBuilder.httpRequest.containsHeader("foo"));
    }

    @Test
    void addPlain() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        requestBuilder.addPlain("");
        assertEquals("", requestBuilder.stringContent);
        requestBuilder.addPlain("foobar");
        assertEquals("foobar", requestBuilder.stringContent);
        assertEquals("text/plain", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addPlainWithErrors() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addPlain(null));
        assertNull(requestBuilder.stringContent);
        assertNull(requestBuilder.contentType);
    }

    @Test
    void addJson() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        requestBuilder.addJson("{}");
        assertEquals("{}", requestBuilder.stringContent);
        assertEquals("application/json", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addJsonWithErrors() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addJson(null));
        assertNull(requestBuilder.stringContent);
        assertNull(requestBuilder.contentType);
    }

    @Test
    void addForm() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "POST", "http://www.example.com");
        requestBuilder.addForm("foo", "bar");
        assertTrue(requestBuilder.formContent.contains(new BasicNameValuePair("foo", "bar")));
        requestBuilder.addForm("foo2", "");
        assertTrue(requestBuilder.formContent.contains(new BasicNameValuePair("foo2", "")));
        assertEquals("application/x-www-form-urlencoded", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addFormWithErrors() {
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addForm(null, "bar"));
        assertThrows(Exception.class, ()->requestBuilder.addForm("foo", null));
        assertThrows(Exception.class, ()->requestBuilder.addForm(null, null));
        assertThrows(Exception.class, ()->requestBuilder.addForm("", ""));
        assertNull(requestBuilder.contentType);
    }

    @Test
    void addFormDataString() {

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addFormDataString("foo", "bar");
        requestBuilder.addFormDataString("foo2", "");
        requestBuilder.addFormDataString("foo3", "bar", "text/plain; charset=UTF-8");
        assertEquals("multipart/form-data", requestBuilder.contentType.getMimeType());
    }

    @Test
    void addFormDataStringWithErrors(){

        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addFormDataString("foo", null));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataString(null, "bar"));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataString("", "bar"));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataString("foo", "bar", "text/plain; charset=UTF-9"));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataString("foo", "bar", ","));
        assertNull(requestBuilder.contentType);
    }

    @Test
    void addFormDataFile() throws IOException {

        File file = File.createTempFile("test", ".tmp");
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        requestBuilder.addFormDataFile("foo", file);
        requestBuilder.addFormDataFile("foo2", file, "rename.tmp");
        requestBuilder.addFormDataFile("foo3", file, "rename.tmp", "application/octet-stream");
        assertEquals("multipart/form-data", requestBuilder.contentType.getMimeType());
    }
    @Test
    void addFormDataFileWithErrors() throws IOException {

        File file = File.createTempFile("test", ".tmp");
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", "http://www.example.com");
        assertThrows(Exception.class, ()->requestBuilder.addFormDataFile("foo", null));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataFile(null, file));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataFile("", file));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataFile("foo", file, null));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataFile("foo", file, ""));
        assertThrows(Exception.class, ()->requestBuilder.addFormDataFile("foo", file, "rename.tmp", ","));
        assertNull(requestBuilder.contentType);
    }

    @Test
    void setEntity() throws IOException {

        String url = "http://www.example.com";
        File file = File.createTempFile("test", ".tmp");
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", url);
        requestBuilder.setEntity();
        assertNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "HEAD", url);
        requestBuilder.setEntity();
        assertNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addPlain("foobar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addJson("{}");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addForm("foo", "bar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addFormDataString("foo", "bar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "POST", url);
        requestBuilder.addFormDataFile("foo", file);
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "PUT", url);
        requestBuilder.addPlain("foobar");
        requestBuilder.setEntity();
        assertNotNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "DELETE", url);
        requestBuilder.setEntity();
        assertNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "TRACE", url);
        requestBuilder.setEntity();
        assertNull(requestBuilder.httpEntity);

        requestBuilder = new RequestBuilder(httpUtils, "OPTIONS", url);
        requestBuilder.setEntity();
        assertNull(requestBuilder.httpEntity);

    }

    @Test
    void request() {

        String url = "http://www.example.com";
        RequestBuilder requestBuilder = new RequestBuilder(httpUtils, "GET", url);
        requestBuilder.request();
        assertNotNull(httpUtils.httpResponse);
    }
}