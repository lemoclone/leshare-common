package com.hzleshare.common.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

    private static final OkHttpClient client =
        new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(10, 10, TimeUnit.MINUTES))
            .build();

    public static final MediaType XML
        = MediaType.parse("application/xml; charset=utf-8");
    public static final MediaType JSON
        = MediaType.parse("application/json; charset=utf-8");

    public static String execute(Request request) throws IOException {
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static ResponseBody executeToResponseBody(Request request) throws IOException {
        Response response = client.newCall(request).execute();
        return response.body();
    }

    public static String get(String url) throws IOException {
        final Request request = new Request.Builder()
            .get().url(url).build();

        return execute(request);
    }

    public static String post(String url, String requestBody, MediaType mediaType)
        throws IOException {
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        return execute(request);
    }

    public static ResponseBody postToResponseBody(String url, String requestBody,
        MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        return executeToResponseBody(request);
    }

    /**
     * 开启异步线程访问网络
     */
    public static void enqueue(Request request, Callback responseCallback) {
        client.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     */
    public static void enqueue(Request request) {
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

}
