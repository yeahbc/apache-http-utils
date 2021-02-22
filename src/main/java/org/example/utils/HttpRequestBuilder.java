package org.example.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestBuilder {

    private HttpUtils httpUtils;
    private HttpUriRequest httpRequest;
    private ContentType contentType;
    private String stringContent = "";
    private List<NameValuePair> formContent = new ArrayList<>();
    private MultipartEntityBuilder formDataContent = MultipartEntityBuilder.create();

    public HttpRequestBuilder(HttpUtils httpUtils, String requestMethod, String url) {

        this.httpUtils = httpUtils;
        if(requestMethod.contentEquals("GET")){
            httpRequest = new HttpGet(url);
        }else if(requestMethod.contentEquals("POST")){
            httpRequest = new HttpPost(url);
        }
    }

    public HttpRequestBuilder addHeader(String name, String value){
        httpRequest.setHeader(name, value);
        return this;
    }

    public HttpRequestBuilder addText(String textString){
        contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);
        stringContent = textString;
        return this;
    }

    public HttpRequestBuilder addJson(String jsonString){
        contentType = ContentType.create("application/json", StandardCharsets.UTF_8);
        stringContent = jsonString;
        return this;
    }

    public HttpRequestBuilder addForm(String name, String value){
        contentType = ContentType.create("application/x-www-form-urlencoded", StandardCharsets.UTF_8);
        formContent.add(new BasicNameValuePair(name, value));
        return this;
    }



    public ContentType convert(String contentType){

        try{
            String mimeType = null;
            List<NameValuePair> nvps = new ArrayList<>();
            for(String param : contentType.split(";")){
                if(mimeType == null){
                    mimeType = param.trim();
                }else{
                    if(param.matches("\\s+=.*")) {
                        String name = param.substring(0, param.indexOf("=")).trim();
                        String value = param.substring(param.indexOf("=") + 1).trim();
                        nvps.add(new BasicNameValuePair(name, value));
                    }
                }
            }
            return ContentType.create(mimeType, nvps.toArray(new NameValuePair[0]));
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public HttpRequestBuilder addFormData(String name, String value){
        return addFormData(name, value, "text/plain; charset=UTF-8");
    }

    public HttpRequestBuilder addFormData(String name, String value, String contentTypeString){

        contentType = ContentType.create("multipart/form-data",
                new BasicNameValuePair("boundary", "----------------=="));
        if(contentTypeString == null || convert(contentTypeString) == null){
            throw new RuntimeException("Invalid content-type string: " + contentTypeString);
        }else {
            formDataContent.addTextBody(name, value, convert(contentTypeString));
        }
        return this;
    }

    public HttpRequestBuilder addFormData(String name, File file){
        return addFormData(name, file, null);
    }

    public HttpRequestBuilder addFormData(String name, File file, String fileName){
        return addFormData(name, file, fileName, "application/octet-stream");
    }

    public HttpRequestBuilder addFormData(String name, File file, String fileName, String contentTypeString){

        contentType = ContentType.create("multipart/form-data",
                new BasicNameValuePair("boundary", "----------------=="));
        if(contentTypeString == null || convert(contentTypeString) == null){
            throw new RuntimeException("Invalid content-type string: " + contentTypeString);
        }else {
            formDataContent.addBinaryBody(name, file, convert(contentTypeString), (fileName != null )? fileName : file.getName());
        }
        return this;
    }

    public int request(){

        try{

            if(!(httpRequest instanceof HttpGet)) {
                if (!httpRequest.containsHeader("Content-Type") && contentType != null) {
                    httpRequest.setHeader("ContentType", contentType.toString());
                }

                HttpEntity entity = null;
                if (contentType.getMimeType().matches("^text/plain|application/json$")) {
                    entity = new StringEntity(stringContent, StandardCharsets.UTF_8);
                } else if (contentType.getMimeType().contentEquals("application/x-www-form-urlencoded")) {
                    entity = new UrlEncodedFormEntity(formContent, StandardCharsets.UTF_8);
                } else if (contentType.getMimeType().contentEquals("multipart/form-data")) {
                    entity = formDataContent.build();
                }

                ((HttpPost) httpRequest).setEntity(entity);
            }

            CloseableHttpResponse httpResponse = httpUtils.getHttpClient().execute(httpRequest);
            httpUtils.setHttpResponse(httpResponse);
            return httpResponse.getStatusLine().getStatusCode();

        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
