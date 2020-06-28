package com.hummer.core.component.network;

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

public class HMNetworking {
    /**
     * Http请求
     * @param method 请求方式GET or POST
     * @param urlString url string
     * @param callback callback 回调
     */
    public static void sendHttpRequest(HMNetworkMethod method, final String urlString, IRequestCallback callback) {
        switch (method) {
            case GET:{
                sendHttpGetRequest(urlString,callback);
                break;
            }
            case POST: {
                sendHttpPostRequest(urlString, callback);
                break;
            }
        }
    }

    /**
     * Get请求 HTTP
     * @param urlString url string
     * @param callback 回调
     */
    private static void sendHttpGetRequest(final String urlString, IRequestCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(8000);

                    //响应码 非200
                    if (httpURLConnection.getResponseCode() != 200) {
                        if (callback != null) {
                            callback.onError(new Exception("code: " + httpURLConnection.getResponseCode()));
                            return;
                        }
                    }

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    String reponse = convertInputStreamToString(bufferedInputStream);

                    bufferedInputStream.close();
                    inputStream.close();

                    if (callback != null) {
                        callback.onComplete(reponse);
                    }
                }catch (MalformedURLException e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * Post请求 HTTP
     * @param urlString url string
     * @param callback 回调
     */
    private static void sendHttpPostRequest(final String urlString, IRequestCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection httpURLConnection = null;
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(8000);

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
                        if (callback != null) {
                            callback.onError(null);
                            return;
                        }
                    }

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    String reponse = convertInputStreamToString(bufferedInputStream);

                    bufferedInputStream.close();
                    inputStream.close();

                    if (callback != null) {
                        callback.onComplete(reponse);
                    }
                }catch (MalformedURLException e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onError(e);
                    }
                }finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }


    private static String convertInputStreamToString(InputStream inputStream) {
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
