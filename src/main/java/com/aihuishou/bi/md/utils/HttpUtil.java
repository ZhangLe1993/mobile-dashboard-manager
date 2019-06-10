package com.aihuishou.bi.md.utils;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static String ENCODED = "UTF-8";

    public static JSONObject urlGet(String p_url, Map<String, String> param) throws Exception {
        HttpMethod method = null;
        InputStream inputStream = null;
        BufferedReader br = null;
        JSONObject obj = null;
        long startTime = System.currentTimeMillis();
        int retry = 3;
        while (obj == null && retry > 0) {
            retry--;
            try {
                org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
                client.setTimeout(60 * 1000);
                String par = "";
                StringBuilder url = new StringBuilder(p_url);
                if (param != null && !param.isEmpty()) {
                    Iterator ite = param.entrySet().iterator();
                    while (ite.hasNext()) {
                        Map.Entry en = (Map.Entry) ite.next();
                        String key = en.getKey().toString();
                        String value = en.getValue().toString();
                        if (par.trim().length() == 0) {
                            par = "?" + key + "=" + URLEncoder.encode(value, ENCODED);
                        } else {
                            par = "&" + key + "=" + URLEncoder.encode(value, ENCODED);
                        }
                        url.append(par);
                    }
                }
                method = new GetMethod(url.toString());
                method.setRequestHeader("Connection", "close");//自动关闭连接,避免close_wait
                client.executeMethod(method);

                inputStream = method.getResponseBodyAsStream();
                br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuffer = new StringBuilder();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }

                String response = stringBuffer.toString();
                obj = JSONObject.fromObject(response);
                int statusCode = method.getStatusCode();
                if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_NO_CONTENT) {
                    method.abort();
                    throw new Exception("url[" + p_url + "]访问报错;response is:" + response);
                }
                long endTime = System.currentTimeMillis();
                LOGGER.info("end url is:" + url.toString() + ";param" + param + ";cost time:" + (endTime - startTime) + ";result:" + response);
                return obj;
            } catch (Exception ex) {
                LOGGER.warn("", ex);
                throw ex;
            } finally {
                if (br != null) {
                    br.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (method != null) {
                    method.releaseConnection();
                }
            }
        }
        return obj;
    }

    public static byte[] urlPost(String url, JSONObject json) throws Exception {
        //JSONObject array = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        HttpPost post = new HttpPost(url);
        byte[] response = null;
        int retry = 3;
        while (response == null && retry > 0) {
            retry--;
            try {

                StringEntity s = new StringEntity(json.toString(),Charset.forName("UTF-8"));
                //s.setContentEncoding("utf-8");
                //s.setContentType("application/json");//发送json数据需要设置contentType "application/json"
                post.addHeader("Content-type","application/json; charset=utf-8");
                post.setHeader("Accept","application/json");
                post.setEntity(s);
                httpClient = HttpClients.createDefault();
                httpResponse = httpClient.execute(post);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = httpResponse.getEntity();
                    response = EntityUtils.toByteArray(httpResponse.getEntity());
                    // 返回json格式：
                    //response = JSONObject.fromObject(result);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
                LOGGER.error("ERROR: post url:" + url + "params: " + json.toString());
            } finally {
                try {
                    httpClient.close();
                    if (httpResponse != null) {
                        httpResponse.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return response;
    }
}
