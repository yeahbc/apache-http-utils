package org.example.utils;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
        return new HttpUtils(HttpClients.createDefault());
    }

    public static ClientBuilder createCustom(){
        return new ClientBuilder();
    }

    public RequestBuilder GET(String url){
        return new RequestBuilder(this, "GET", url);
    }

    public RequestBuilder POST(String url){
        return new RequestBuilder(this, "POST", url);
    }

    public int getStatusCode(){
        return (httpResponse != null) ? httpResponse.getStatusLine().getStatusCode() : -1;
    }

    public String[] getHeaders(String headerName){

        List<String> headerList = new ArrayList<>();
        for(Header header : httpResponse.getAllHeaders()){
            if(header.getName().equalsIgnoreCase(headerName)){
                headerList.add(header.getValue());
            }
        }
        return headerList.toArray(new String[0]);
    }

    public void consume() {

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

        try{
            Charset charset = null;
            if(httpResponse.containsHeader("Content-Type")){
                String contentTypeString = httpResponse.getFirstHeader("Content-Type").getValue();
                charset = RequestBuilder.convertToCharset(contentTypeString);
            }
            return EntityUtils.toString(httpResponse.getEntity(), charset == null ? StandardCharsets.UTF_8 : charset);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile(){

        try{
            File file = File.createTempFile("response-", ".tmp", new File(System.getProperty("user.home")));
            httpResponse.getEntity().writeTo(new FileOutputStream(file));
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
        }
    }
}
