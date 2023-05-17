package com.register.agent.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestUtil {

    private static Logger log = LoggerFactory.getLogger(HttpRequestUtil.class);
    private static final int connectionTimeout = 20*1000;
    private static final int connectionRequestTimeout = 10*1000;
    private static final int soTimeout = 20*1000;

    private static PoolingHttpClientConnectionManager poolConnManager;
    private static final int maxTotalPool = 200;
    private static final int maxConPerRoute = 20;

    static{
        init();
    }

    private static void init(){
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // Increase max total connection to 200
            poolConnManager.setMaxTotal(maxTotalPool);
            // Increase default max connection per route to 20
            poolConnManager.setDefaultMaxPerRoute(maxConPerRoute);
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(soTimeout).build();
            poolConnManager.setDefaultSocketConfig(socketConfig);
        } catch (Exception e) {
            log.error("鏈接池初始化異常", e);
        }
    }

    public static CloseableHttpClient getHttpClient(){
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(soTimeout)
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager).setDefaultRequestConfig(config).build();
        if(poolConnManager!=null&&poolConnManager.getTotalStats()!=null){
            log.info("now client pool "+poolConnManager.getTotalStats().toString());
        }
        return httpClient;
    }

    /**
     * 根据url获取http/https请求的client
     * @return
     */
    @SuppressWarnings("unused")
    private static CloseableHttpClient getHttpClient2(String url) {
        CloseableHttpClient httpClient= null;
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(soTimeout)
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
        if(url.startsWith("http://")){
            httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
        }else if(url.startsWith("https://")){
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    //信任所有
                    public boolean isTrusted(X509Certificate[] chain,
                                             String authType) throws CertificateException {
                        return true;
                    }
                }).build();
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                return HttpClients.custom().setDefaultRequestConfig(config).setSSLSocketFactory(sslsf).build();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }
        return httpClient;
    }

    /**
     * 参数以json格式发送
     * @param url
     * @param jsonStr ：{"key1":"value1","key2":"value3"}
     * @param charset
     * @return
     */
    public static String postMethodByJsonStream(String url,Map<String, String> headerMap, String jsonStr,String charset){
        String html = "";
        CloseableHttpClient httpClient = getHttpClient();
        if(httpClient == null){
            return html;
        }
        CloseableHttpResponse httpResponse = null;
        try{
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("content-type", "application/json");
            if(headerMap!=null){
                for (Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            StringEntity se = new StringEntity(jsonStr,charset);
            httpPost.setEntity(se);
            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
//					html = EntityUtils.toString(httpEntity, charset);
//					EntityUtils.consume(httpEntity);
                    html = httpEntity2String(httpEntity, charset);
                }
            }else{
                String msg = "访问失败！！HTTP_STATUS=" + statusCode;
                throw new HttpException(msg);
            }
            httpPost.abort();
            return html;
        } catch (Exception e) {
            log.error(HttpRequestUtil.class.getName() + " postMethodByJsonStream error:", e);
        } finally {
            try {
                if(httpResponse!=null){
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;
    }
    /**
     * 普通传参方式，body为key1=value1&key2=value2&key3=value3格式
     * @param url
     * @param body
     * @param charset
     * @return
     */
    @Deprecated
    public static String postMethod(String url,String body,String charset){
        Map<String,String> map = urlStringToMap(body);
        return postMethod(url, map, charset);
    }
    /**
     *  post 方法
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public static String postMethod(String url,Map<String,String> map,String charset){
        String html = "";
        CloseableHttpClient httpClient = getHttpClient();
        if(httpClient == null){
            return html;
        }
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if(map!=null){
                List<BasicNameValuePair> list=new ArrayList<BasicNameValuePair>();
                for (Entry<String, String> entry : map.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                if(list.size() > 0){
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                    httpPost.setEntity(entity);
                }
            }
            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
//					html = EntityUtils.toString(httpEntity, charset);
//					EntityUtils.consume(httpEntity);
                    html = httpEntity2String(httpEntity, charset);
                }
            }else{
                String msg = "访问失败！！HTTP_STATUS=" + statusCode;
                throw new HttpException(msg);
            }
            httpPost.abort();
            return html;
        } catch (Exception e) {
            log.error(HttpRequestUtil.class.getName() + " postMethod error:", e);
        } finally {
            try {
                if(httpResponse!=null){
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;

    }

    public static String postMethodMoveUrl(String url,Map<String,String> map,String charset){
        return postMethod(url, map, charset,null);
    }

    public static String postMethod(String url,Map<String,String> map,String charset,Map<String, String> headerMap){
        String html = "";
        CloseableHttpClient httpClient = getHttpClient();
        if(httpClient == null){
            return html;
        }
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            if(headerMap!=null){
                for (Entry<String, String> entry : headerMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            if(map!=null){
                List<BasicNameValuePair> list=new ArrayList<BasicNameValuePair>();
                for (Entry<String, String> entry : map.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                if(list.size() > 0){
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                    httpPost.setEntity(entity);
                }
            }
            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
//					html = EntityUtils.toString(httpEntity, charset);
//					EntityUtils.consume(httpEntity);
                    html = httpEntity2String(httpEntity, charset);
                }
                httpPost.abort();
            }else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                    || statusCode == HttpStatus.SC_SEE_OTHER || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) {
                log.info("--------HttpStatus:"+statusCode);
                Header header = httpResponse.getFirstHeader("location");
                if (header == null) {
                    return html;
                }
                String newuri = header.getValue();
                if ((newuri == null) || (newuri.equals(""))) {
                    newuri = "/";
                }
                return urlEdit(url, newuri);
            }else{
                String msg = "访问失败！！HTTP_STATUS=" + statusCode;
                throw new HttpException(msg);
            }
            return html;
        } catch (Exception e) {
            log.error(HttpRequestUtil.class.getName() + " postMethod error:", e);
        } finally {
            try {
                if(httpResponse!=null){
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;

    }
    /**
     * get请求方法，如果map中包含中文，推荐使用post方法发送
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public static String getMethod(String url,Map<String,String> map,String charset){
        String body = mapToUrlString(map,charset);
        if(StringUtils.isNotBlank(body)){
            body = "?"+body;
        }
        return getMethod(url+body, charset);
    }
    /**
     * 通过get方法获取html
     * @param url
     * @param charset
     * @return
     */
    public static String getMethod(String url, String charset) {
        String html = "";
        CloseableHttpClient httpClient = getHttpClient();
        if(httpClient == null){
            return html;
        }
        CloseableHttpResponse response = null;
        try {
            HttpGet httpget = new HttpGet(url);
            response = httpClient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String msg = "访问失败！！HTTP_STATUS=" + statusCode;
                throw new HttpException(msg);
            }
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                html = httpEntity2String(httpEntity, charset);
            }
            httpget.abort();
        } catch (Exception e) {
            log.error("访问地址" + url + "时报错", e);
        } finally {
            try {
                if(response!=null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;
    }

    /**
     * 最简单的get方法
     * @param url
     * @return
     */
    public static String getMethod(String url){
        String html = "";
        CloseableHttpClient httpClient = getHttpClient();
        if(httpClient == null){
            return html;
        }
        try {
            HttpGet httpGet = new HttpGet(url);
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            html = httpClient.execute(httpGet, responseHandler);
            httpGet.abort();
        } catch (ClientProtocolException e) {
            log.error(HttpRequestUtil.class.getName() + " getMethod error:", e);
        } catch (IOException e) {
            log.error(HttpRequestUtil.class.getName() + " getMethod error:", e);
        } finally {
        }
        return html;
    }

    private static String httpEntity2String(HttpEntity httpEntity,String charset) throws UnsupportedOperationException, IOException{
        String html = "";
        InputStream instream = httpEntity.getContent();
        if (instream == null) {
            return html;
        }
        try {
            if (httpEntity.getContentLength() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
            }
            int i = (int) httpEntity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            Reader reader = new InputStreamReader(instream, charset);
            CharArrayBuffer buffer = new CharArrayBuffer(i);
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            html = buffer.toString();
        } finally {
            instream.close();
        }
        return html;
    }

    /**
     * map转换为key1=value1&key2=value2&key3=value3格式
     * @return
     */
    public static String mapToUrlString(Map<String, String> map,String charset){
        if(map==null||map.size()==0){
            return "";
        }
        StringBuffer str= new StringBuffer();
        for (Entry<String, String> entry : map.entrySet()) {
            str.append("&");
            str.append(entry.getKey());
            str.append("=");
            try {
                str.append(URLEncoder.encode(entry.getValue(),charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if(str.length()>0){
            return str.substring(1);
        }
        return "";
    }
    /**
     * key1=value1&key2=value2&key3=value3格式转换为map
     * @return
     */
    public static Map<String,String> urlStringToMap(String body){
        Map<String,String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(body)){
            return map;
        }
        String[] ss = body.split("&");
        for (String s : ss) {
            if(s.contains("=")){
                String key = StringUtils.substringBefore(s, "=");
                String value = StringUtils.substringAfter(s, "=");
                map.put(key, value);
            }
        }
        return map;
    }
    public static String urlEdit(String domain, String url) {
        if (StringUtils.isBlank(domain) || StringUtils.isBlank(url) || url.trim().startsWith("http://")
                || url.trim().startsWith("https://")) {
            return url;
        }
        if (url.trim().startsWith("/")) {
            if (domain.indexOf("/", 9) != -1) {
                domain = domain.substring(0, domain.indexOf("/", 9));
            }
            return domain + url;
        } else if (url.trim().startsWith("../../")) {//一次性代码，暂时不支持3个../
            domain = (domain.indexOf("/", 9) != -1) ? domain.substring(0, domain.lastIndexOf("/")) : domain;
            domain = (domain.indexOf("/", 9) != -1) ? domain.substring(0, domain.lastIndexOf("/")) : domain;
            domain = (domain.indexOf("/", 9) != -1) ? domain.substring(0, domain.lastIndexOf("/")) : domain;
            return domain + "/" + url.replace("../", "");
        } else if (url.trim().startsWith("../")) {
            domain = (domain.indexOf("/", 9) != -1) ? domain.substring(0, domain.lastIndexOf("/")) : domain;
            domain = (domain.indexOf("/", 9) != -1) ? domain.substring(0, domain.lastIndexOf("/")) : domain;
            return domain + "/" + url.replace("../", "");
        } else {
            if (domain.indexOf("/", 9) != -1) {
                domain = domain.substring(0, domain.lastIndexOf("/"));
                return domain + "/" + url;
            }
        }
        return null;
    }

}
