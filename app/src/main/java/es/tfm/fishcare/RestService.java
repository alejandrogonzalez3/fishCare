package es.tfm.fishcare;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class RestService {
    private static String BACKEND_URL = "http://192.168.0.20:8080/";
    private static OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getClient() {
        return client;
    }

    public static HttpUrl.Builder getUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        return urlBuilder;
    }

    public static HttpUrl.Builder getSensorValueUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("sensorValue");
        urlBuilder.addPathSegment("");
        urlBuilder.addQueryParameter("page", "0");
        urlBuilder.addQueryParameter("size", "15");
        return urlBuilder;
    }
}
