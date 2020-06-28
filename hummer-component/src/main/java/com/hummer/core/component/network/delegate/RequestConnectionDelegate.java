package com.hummer.core.component.network.delegate;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.hummer.core.component.network.HMRequest;
import com.hummer.core.component.network.IRequestCallback;
import com.hummer.core.component.utility.UIThreadUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 为 HMRequest 模块提供一个基于 HttpURLConnection 默认实现
 * HMRequest 缺省下使用这个模块，业务放根据需要可替换这一模块
 */
public class RequestConnectionDelegate implements IRequestDelegate {

    @Override
    public void send(String url, String method,
                     int timeout, HashMap<String, Object> header,
                     HashMap<String, Object> param, IRequestCallback callback) {
        if (HMRequest.METHOD_GET.equals(method)) {
            sendHttpGetRequest(url, method, timeout, header, param, callback);
        } else if (HMRequest.METHOD_POST.equals(method)) {
            sendHttpPostRequest(url, method, timeout, header, param, callback);
        }
    }

    /**
     * Get请求 HTTP
     */
    private void sendHttpGetRequest(String url, String method,
                                    int timeout, HashMap<String, Object> header,
                                    HashMap<String, Object> param, IRequestCallback callback){
        String urlString = url + param2String(param);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(timeout);
                    httpURLConnection.setReadTimeout(8000);
                    configHttpURLConn(httpURLConnection, header);
                    //响应码 非200
                    if (httpURLConnection.getResponseCode() != 200) {
                        int responseCode = httpURLConnection.getResponseCode();
                        if (callback != null) {
                            UIThreadUtil.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(null);
                                }
                            });
                            return;
                        }
                    }

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    String reponse = convertInputStreamToString(bufferedInputStream);

                    bufferedInputStream.close();
                    inputStream.close();

                    UIThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onComplete(reponse);
                            }
                        }
                    });
                }catch (MalformedURLException e) {
                    UIThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                } catch (IOException e) {
                    UIThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * Post请求 HTTP
     */
    private void sendHttpPostRequest(String url, String method,
                                     int timeout, HashMap<String, Object> header,
                                     HashMap<String, Object> param, IRequestCallback callback){
        String urlString = url + param2String(param);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(timeout);
                    httpURLConnection.setReadTimeout(8000);

                    configHttpURLConn(httpURLConnection, header);

                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoInput(true);

                    String data = "";//这里解析参数 暂时不添加
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

                    bufferedOutputStream.write(data.getBytes());
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    outputStream.close();

                    //响应码 非200
                    if (httpURLConnection.getResponseCode() != 200) {
                        int responseCode = httpURLConnection.getResponseCode();
                        if (callback != null) {
                            UIThreadUtil.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(null);
                                }
                            });
                            return;
                        }
                    }

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    String reponse = convertInputStreamToString(bufferedInputStream);

                    bufferedInputStream.close();
                    inputStream.close();
                    UIThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onComplete(reponse);
                            }
                        }
                    });
                }catch (MalformedURLException e) {
                    UIThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                } catch (IOException e) {
                    UIThreadUtil.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onError(e);
                            }
                        }
                    });
                }finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 设置HttpURLConnection property e.g. request header
     * @param httpURLConnection
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configHttpURLConn(HttpURLConnection httpURLConnection,
                                   HashMap<String, Object> header){
        if (header == null) return;
        Iterator iterator = ((HashMap) header).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (key.equals("Authorization")){
                String userCredentials = "username:password";
                String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
                value = basicAuth;
            }
            httpURLConnection.setRequestProperty(key,(String)value);
        }
    }

    /**
     * 将参数转成string
     * @return
     */
    private String param2String(HashMap<String, Object> param){
        if (param == null) return "";

        String ret = "";

        for (Object o : ((HashMap) param).entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            ret += "&" + key + "=" + value.toString();
        }

        return ret;
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
