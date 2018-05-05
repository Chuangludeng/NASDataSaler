package com.littledemo.Util;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;


/**
 * @Description
 * @author 许凯勋
 * @date 2016年10月14日 下午4:04:11
 */
/**
 * 分装一个http请求的工具类
 *
 * @author 顾炜【guwei】 on 14-4-22.下午3:17
 */
public class HttpClientUtil {


    /**
     * 初始化HttpClient
     */
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * POST方式调用
     *
     * @param url
     * @param json json字符串
     * @return 响应字符串
     * @throws java.io.UnsupportedEncodingException
     */
    public String executeByPOST(String url, String json) {
        HttpPost post = new HttpPost(url);

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseJson = null;
        try {
            if (json != null) {
                StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
                entity.setContentEncoding("UTF-8");
                // 发送Json格式的数据请求
                entity.setContentType("application/json");
                post.setEntity(entity);
            }
            responseJson = httpClient.execute(post, responseHandler);
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            httpClient.getConnectionManager().closeExpiredConnections();
            httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);
        }
        return responseJson;
    }

    /**
     * Get方式请求
     *
     * @param url 带参数占位符的URL，例：http://ip/User/user/center.aspx?_action=GetSimpleUserInfo&codes={0}&email={1}
     * @param params 参数值数组，需要与url中占位符顺序对应
     * @return 响应字符串
     * @throws java.io.UnsupportedEncodingException
     */
    public String executeByGET(String url, Object[] params) {
        String messages = MessageFormat.format(url, params);
        HttpGet get = new HttpGet(messages);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseJson = null;
        try {
            responseJson = httpClient.execute(get, responseHandler);
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            httpClient.getConnectionManager().closeExpiredConnections();
            httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);
        }
        return responseJson;
    }

    /**
     * @param url
     * @return
     */
    public String executeByGET(String url) {
        HttpGet get = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseJson = null;
        try {
            responseJson = httpClient.execute(get, responseHandler);
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            httpClient.getConnectionManager().closeExpiredConnections();
            httpClient.getConnectionManager().closeIdleConnections(30, TimeUnit.SECONDS);
        }
        return responseJson;
    }

}