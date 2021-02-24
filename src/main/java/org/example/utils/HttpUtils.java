package org.example.utils;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpUtils {

    final CloseableHttpClient httpClient;
    CloseableHttpResponse httpResponse;

    public HttpUtils(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    public static HttpUtils createDefault(){
        return new ClientBuilder().defaultBuild();
    }

    public static ClientBuilder createCustom(){
        return new ClientBuilder();
    }

    public RequestBuilder GET(String url){
        return new RequestBuilder(this, "GET", url);
    }

    public RequestBuilder HEAD(String url){
        return new RequestBuilder(this, "HEAD", url);
    }

    public RequestBuilder POST(String url){
        return new RequestBuilder(this, "POST", url);
    }

    public RequestBuilder PUT(String url){
        return new RequestBuilder(this, "PUT", url);
    }

    public RequestBuilder DELETE(String url){
        return new RequestBuilder(this, "DELETE", url);
    }

    public RequestBuilder TRACE(String url){
        return new RequestBuilder(this, "TRACE", url);
    }

    public RequestBuilder OPTIONS(String url){
        return new RequestBuilder(this, "OPTIONS", url);
    }

    public int getStatusCode(){
        return (httpResponse != null) ? httpResponse.getStatusLine().getStatusCode() : -1;
    }

    public String[] getHeaders(String headerName){

        if(httpResponse == null){
            return null;
        }

        List<String> headerList = new ArrayList<>();
        for(Header header : httpResponse.getAllHeaders()){
            if(header.getName().equalsIgnoreCase(headerName)){
                headerList.add(header.getValue());
            }
        }
        return headerList.toArray(new String[0]);
    }

    public void consume() {

        if(httpResponse == null){
            throw new RuntimeException("No http response is returned");
        }
        try{
            EntityUtils.consume(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getContent(){

        if(httpResponse == null){
            throw new RuntimeException("No http response is returned");
        }

        Charset charset = null;

        try {
            if (httpResponse.containsHeader("Content-Type")) {
                String contentTypeString = httpResponse.getFirstHeader("Content-Type").getValue();
                charset = RequestBuilder.convertToCharset(contentTypeString);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            return EntityUtils.toString(httpResponse.getEntity(), charset == null ? StandardCharsets.UTF_8 : charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile(){

        if(httpResponse == null){
            throw new RuntimeException("No http response is returned");
        }
        try{
            File file = File.createTempFile("response-", ".tmp", new File(System.getProperty("user.home")));
            httpResponse.getEntity().writeTo(new FileOutputStream(file));
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(){

        try{
            if(httpClient != null){
                httpClient.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
