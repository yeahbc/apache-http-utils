package org.example.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestBuilder {

    HttpUtils httpUtils;
    HttpUriRequest httpRequest;
    ContentType contentType;
    String stringContent;
    List<NameValuePair> formContent;
    MultipartEntityBuilder formDataContent;
    HttpEntity httpEntity;

    public RequestBuilder(HttpUtils httpUtils, String requestMethod, String url) {

        Args.notNull(httpUtils, "httpUtils");
        Args.notBlank(requestMethod, "requestMethod");
        Args.notBlank(url, "url");

        List<String> methods = Arrays.asList(new String[]{"GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS"});
        switch(methods.indexOf(requestMethod)){
            case 0:
                httpRequest = new HttpGet(url);
                break;
            case 1:
                httpRequest = new HttpHead(url);
                break;
            case 2:
                httpRequest = new HttpPost(url);
                break;
            case 3:
                httpRequest = new HttpPut(url);
                break;
            case 4:
                httpRequest = new HttpDelete(url);
                break;
            case 5:
                httpRequest = new HttpTrace(url);
                break;
            case 6:
                httpRequest = new HttpOptions(url);
                break;
            case -1:
                throw new RuntimeException("Unsupported http method");
        }
        this.httpUtils = httpUtils;
    }

    public RequestBuilder addHeader(String name, String value){

        Args.notBlank(name, "name");
        Args.notNull(value, "value");
        httpRequest.setHeader(name, value);
        return this;
    }

    public RequestBuilder delHeaders(String name){

        Args.notBlank(name, "name");
        httpRequest.removeHeaders(name);
        return this;
    }

    protected void setContentType(String entityType){

        List<String> list = Arrays.asList(new String[]{"text", "json", "form", "form-data"});
        switch(list.indexOf(entityType)){
            case 0:
                this.contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);
                formContent = null;
                formDataContent = null;
                break;
            case 1:
                this.contentType = ContentType.create("application/json", StandardCharsets.UTF_8);
                formContent = null;
                formDataContent = null;
                break;
            case 2:
                this.contentType = ContentType.create("application/x-www-form-urlencoded", StandardCharsets.UTF_8);
                stringContent = null;
                formDataContent = null;
                break;
            case 3:
                this.contentType = ContentType.create("multipart/form-data",
                        new BasicNameValuePair("boundary", "----------------**"));;
                stringContent = null;
                formContent = null;
                break;
        }
    }

    public RequestBuilder addPlain(String plainString){

        Args.notNull(plainString, "plainString");
        stringContent = plainString;
        setContentType("text");
        return this;
    }

    public RequestBuilder addJson(String jsonString){

        Args.notNull(jsonString, "jsonString");
        stringContent = jsonString;
        setContentType("json");
        return this;
    }

    public RequestBuilder addForm(String name, String value){

        Args.notNull(name, "name");
        Args.notNull(value, "value");

        if(formContent == null){
            formContent = new ArrayList<>();
        }
        formContent.add(new BasicNameValuePair(name, value));
        setContentType("form");
        return this;
    }

    public static Charset convertToCharset(String contentType){

        Args.notBlank(contentType, "contentType");
        if(convertToContentType(contentType) != null){
            return convertToContentType(contentType).getCharset();
        }else{
            return null;
        }
    }

    public static ContentType convertToContentType(String contentType){

        Args.notBlank(contentType, "contentType");
        try{
            String mimeType = null;
            List<NameValuePair> nvps = new ArrayList<>();
            for(String param : contentType.split(";")){
                if(mimeType == null){
                    mimeType = param.trim();
                }else{
                    if(param.matches(".+=.*")) {
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

    public RequestBuilder addFormData(String name, String value){
        return addFormData(name, value, "text/plain; charset=UTF-8");
    }

    public RequestBuilder addFormData(String name, String value, String contentTypeString){

        Args.notNull(name, "name");
        Args.notNull(value, "value");
        Args.notBlank(contentTypeString, "contentTypeString");

        if(convertToContentType(contentTypeString) == null){
            throw new RuntimeException("Invalid contentTypeString argument: " + contentTypeString);
        }

        if(formDataContent == null){
            formDataContent = MultipartEntityBuilder.create();
            formDataContent.setBoundary("----------------**");
        }
        formDataContent.addTextBody(name, value, convertToContentType(contentTypeString));
        setContentType("form-data");
        return this;
    }

    public RequestBuilder addFormData(String name, File file){
        return addFormData(name, file, null);
    }

    public RequestBuilder addFormData(String name, File file, String fileName){
        return addFormData(name, file, fileName, "application/octet-stream");
    }

    public RequestBuilder addFormData(String name, File file, String fileName, String contentTypeString){

        Args.notNull(name, "name");
        Args.notNull(file, "file");
        Args.notBlank(contentTypeString, "contentTypeString");

        if(convertToContentType(contentTypeString) == null){
            throw new RuntimeException("Invalid contentTypeString argument: " + contentTypeString);
        }

        if(formDataContent == null){
            formDataContent = MultipartEntityBuilder.create();
            formDataContent.setBoundary("----------------**");
        }
        formDataContent.addBinaryBody(name, file, convertToContentType(contentTypeString), (fileName != null )? fileName : file.getName());
        setContentType("form-data");

        return this;
    }

    protected void setEntity(){

        if(httpRequest instanceof HttpGet || httpRequest instanceof HttpHead || httpRequest instanceof HttpDelete ||
                httpRequest instanceof HttpTrace || httpRequest instanceof HttpOptions){
            return;
        }

        if(stringContent != null){
            httpEntity = new StringEntity(stringContent, StandardCharsets.UTF_8);
        }
        if(formContent != null){
            httpEntity = new UrlEncodedFormEntity(formContent, StandardCharsets.UTF_8);
        }
        if(formDataContent != null){
            httpEntity = formDataContent.build();
        }

        if(httpRequest instanceof HttpPost){
            if(httpEntity != null){
                ((HttpPost)httpRequest).setEntity(httpEntity);
                if (!httpRequest.containsHeader("Content-Type")) {
                    httpRequest.setHeader("ContentType", contentType.toString());
                }
            }else{
                throw new RuntimeException("Lack of http request entity");
            }
        }

        if(httpRequest instanceof HttpPut){
            if(httpEntity != null) {
                ((HttpPut) httpRequest).setEntity(httpEntity);
                if (!httpRequest.containsHeader("Content-Type")) {
                    httpRequest.setHeader("ContentType", contentType.toString());
                }
            }else{
                throw new RuntimeException("Lack of http request entity");
            }
        }
    }

    public void request(){

        httpUtils.httpResponse = null;
        try{
            setEntity();
            httpUtils.httpResponse = httpUtils.httpClient.execute(httpRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
