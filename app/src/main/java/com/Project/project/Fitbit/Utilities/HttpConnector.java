package com.Project.project.Fitbit.Utilities;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpConnector {
    private static HttpConnector connector;
    private OkHttpClient okHttpClient;

    private HttpConnector() {
        okHttpClient = new OkHttpClient();
    }

    public static HttpConnector getInstance() {
        if (connector == null)
            connector = new HttpConnector();
        return connector;
    }

    public JSONObject run(String url, String accessToken) {
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            switch (response.code()) {
                case 200:
                case 201:
                    return new JSONObject(response.body().string());
                case 401:
                    return new JSONObject("{\"error\":\"user have no permission to read this data\"," +
                            "\"code\":\"401\"}");
                case 403:
                    //user didn't allowed app in Fitbit
                    return new JSONObject("{\"error\":\"user didn't allowed this app to read data\"," +
                            "\"code\":\"403\"}");
                default:
                    System.out.println(response.code() + " : " + response);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
