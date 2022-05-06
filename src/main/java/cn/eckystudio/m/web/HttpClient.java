package cn.eckystudio.m.web;

import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import cn.eckystudio.m.common.KeyValuePair;

/**
 * Created by Ecky on 2018/1/15.
 */

public class HttpClient {
    //http 报文请求头定义
    public RequestMethod RH_REQUST_METHOD = RequestMethod.METHOD_GET;//Request header request method
    public String RH_ENCODE = "utf-8";

    ArrayList<KeyValuePair> mParamesContainer = new ArrayList<KeyValuePair>();

    String mContent = null;

    public HttpClient(){
    }

    public String getHtmlText(String url){
        String ret = null;
        
        URL u = null;
        try {
            u = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setDoInput(true);

            urlConnection.setRequestMethod(RH_REQUST_METHOD.toString());

            if(RH_REQUST_METHOD == RequestMethod.METHOD_POST )
            {
                makePostArgument(urlConnection);
            }

            if(mContent != null){
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(mContent.getBytes());
                urlConnection.getOutputStream().flush();
//                urlConnection.getOutputStream().close(); //不需要关闭，关闭概率性会报  java.util.NoSuchElementException 异常
            }

            int statusCode = urlConnection.getResponseCode();
            if(statusCode == 200) {
                InputStream is = urlConnection.getInputStream();

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                if(is != null){
                       int len;
                       while( (len = is.read(data) )  != -1){
                           os.write(data,0,len);
                       }
                       ret = new String(os.toByteArray(),RH_ENCODE);
                }
            }
            urlConnection.disconnect();//断开服务器连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void reset(){
        mParamesContainer.clear();
        mContent = null;
    }

    public void addArgument(String name,String value){
        mParamesContainer.add(new KeyValuePair<String,String>(name,value));
    }

    public String makeUrlWithArguments(String url){
        if(mParamesContainer.size() == 0)
            return url;

        StringBuilder sb = new StringBuilder();
        sb.append(url + "?");
        for (KeyValuePair<String,String> pair: mParamesContainer) {
            sb.append(pair.getKey() + '=' + pair.getValue() + '&');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public void setContent(String content){
        mContent = content;
    }

    //请求双向认证
    public void requestTwoWayAuthentication(String certificatePath,String password){
        ;
    }

    private void makePostArgument(HttpURLConnection urlConnection){
        if(mParamesContainer.size() == 0)
            return;

        for(KeyValuePair<String,String> pair:mParamesContainer){
            urlConnection.addRequestProperty(pair.getKey(), pair.getValue());
        }
    }




    public static enum RequestMethod{
        METHOD_GET("GET"),
        METHOD_POST("POST");

        private String mValue;
        private  RequestMethod(String method){
               mValue = method;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }
}
