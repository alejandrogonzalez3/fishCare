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
        return urlBuilder;
    }

    public static HttpUrl.Builder getActuatorUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("actuator");
        urlBuilder.addPathSegment("");
        return urlBuilder;
    }

    public static HttpUrl.Builder getActuatorOnUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("actuator");
        urlBuilder.addPathSegment("on");
        return urlBuilder;
    }

    public static HttpUrl.Builder getActuatorOffUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("actuator");
        urlBuilder.addPathSegment("off");
        return urlBuilder;
    }

    public static HttpUrl.Builder getNotificationUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("notification");
        urlBuilder.addPathSegment("");
        return urlBuilder;
    }

    public static HttpUrl.Builder getUserUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("user");
        urlBuilder.addPathSegment("");
        return urlBuilder;
    }

    public static HttpUrl.Builder getHatcheryUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("hatchery");
        urlBuilder.addPathSegment("");
        return urlBuilder;
    }

    public static HttpUrl.Builder getSensorUrlBuilder() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BACKEND_URL).newBuilder();
        urlBuilder.addPathSegment("sensor");
        urlBuilder.addPathSegment("");
        return urlBuilder;
    }
}
