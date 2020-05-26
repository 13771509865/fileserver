package com.yozosoft.fileserver.common.helper;

import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.common.constants.SysConstant;
import com.yozosoft.fileserver.common.entity.HttpResultEntity;
import com.yozosoft.fileserver.common.utils.DefaultResult;
import com.yozosoft.fileserver.common.utils.IResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhoufeng
 * @description http工具类
 * @create 2020-05-12 19:35
 **/
@Component
@Slf4j
public class HttpApiHelper {

    @Autowired
    private CloseableHttpClient httpClient;

    public Boolean isHttpSuccess(IResult<HttpResultEntity> result) {
        if (result.isSuccess()) {
            HttpResultEntity httpResultEntity = result.getData();
            Integer httpCode = httpResultEntity.getCode();
            if (httpCode >= HttpStatus.OK.value() && httpCode < HttpStatus.MULTIPLE_CHOICES.value()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param url    请求地址
     * @param params 请求参数map
     * @description 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     */
    public IResult<HttpResultEntity> doGet(String url, Map<String, Object> params, Map<String, Object> headers) {
        CloseableHttpResponse response = null;
        try {
            response = doGetProcess(url, params, headers);
            HttpResultEntity httpResultEntity = new HttpResultEntity(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                    response.getEntity(), SysConstant.CHARSET));
            return DefaultResult.successResult(httpResultEntity);
        } catch (Exception e) {
            log.error("get请求失败,请求URL为:" + url, e);
            return DefaultResult.failResult(EnumResultCode.E_HTTP_SEND_FAIL.getInfo());
        } finally {
            closeResource(response);
        }
    }

    /**
     * @param url 请求地址
     * @description 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     */
    public IResult<HttpResultEntity> doGet(String url) {
        return this.doGet(url, null, null);
    }

    public IResult<HttpResultEntity> doGet(String url, Map<String, Object> params) {
        return this.doGet(url, params, null);
    }

    /**
     * @param url    请求地址
     * @param params 请求参数map
     * @description 带参数的post请求
     */
    public IResult<HttpResultEntity> doPost(String url, Map<String, Object> params, Map<String, Object> headers) {
        CloseableHttpResponse response = null;
        try {
            response = doPostProcess(url, params, headers);
            HttpResultEntity httpResultEntity = new HttpResultEntity(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                    response.getEntity(), SysConstant.CHARSET));
            return DefaultResult.successResult(httpResultEntity);
        } catch (Exception e) {
            log.error("post请求失败,请求URL为:" + url, e);
            return DefaultResult.failResult(EnumResultCode.E_HTTP_SEND_FAIL.getInfo());
        } finally {
            closeResource(response);
        }
    }

    /**
     * @param url 请求参数
     * @description 不带参数post请求
     */
    public IResult<HttpResultEntity> doPost(String url) {
        return this.doPost(url, null, null);
    }

    public IResult<HttpResultEntity> doPost(String url, Map<String, Object> params) {
        return this.doPost(url, params, null);
    }

    /**
     * @param url     请求地址
     * @param jsonStr 请求参数jsonstr
     * @description 带参数的post请求, json方式
     */
    public IResult<HttpResultEntity> doPostByJson(String url, String jsonStr, Map<String, Object> headers) {
        CloseableHttpResponse response = null;
        try {
            response = doPostByJsonProcess(url, jsonStr, headers);
            HttpResultEntity httpResultEntity = new HttpResultEntity(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                    response.getEntity(), SysConstant.CHARSET));
            return DefaultResult.successResult(httpResultEntity);
        } catch (Exception e) {
            log.error("postByJson请求失败,请求URL为:" + url, e);
            return DefaultResult.failResult(EnumResultCode.E_HTTP_SEND_FAIL.getInfo());
        } finally {
            closeResource(response);
        }
    }

    public IResult<HttpResultEntity> doPostByJson(String url, String jsonStr) {
        return this.doPostByJson(url, jsonStr, null);
    }

    private void addHttpHeader(HttpRequestBase http, Map<String, Object> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                if (entry.getValue() != null) {
                    http.setHeader(entry.getKey(), entry.getValue().toString());
                }
            }
        }
    }

    private CloseableHttpResponse doPostByJsonProcess(String url, String jsonStr, Map<String, Object> headers) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (!StringUtils.isBlank(jsonStr)) {
            StringEntity stringEntity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
        }
        addHttpHeader(httpPost, headers);
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return response;
    }

    private CloseableHttpResponse doHeadProcess(String url, Map<String, Object> headers) throws Exception {
        HttpHead httpHead = new HttpHead(url);
        //httpGet.setConfig(config);
        addHttpHeader(httpHead, headers);
        CloseableHttpResponse response = this.httpClient.execute(httpHead);
        return response;
    }

    private CloseableHttpResponse doGetProcess(String url, Map<String, Object> params, Map<String, Object> headers) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
        //httpGet.setConfig(config);
        addHttpHeader(httpGet, headers);
        CloseableHttpResponse response = this.httpClient.execute(httpGet);
        return response;
    }

    private CloseableHttpResponse doPostProcess(String url, Map<String, Object> params, Map<String, Object> headers) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        //httpPost.setConfig(config);

        if (params != null && !params.isEmpty()) {
            List<NameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    if (entry.getValue() instanceof ArrayList) {
                        for (Object obj : ((ArrayList) entry.getValue())) {
                            list.add(new BasicNameValuePair(entry.getKey(), obj.toString()));
                        }
                    } else {
                        list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                    }
                }
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
        }
        addHttpHeader(httpPost, headers);
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return response;
    }

    private void closeResource(CloseableHttpResponse response) {
        try {
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
        }
    }
}