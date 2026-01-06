package viaRest.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import viaRest.weather.Context;

import java.util.Map;

public class FormatUtil {

    public static void printContextData(String header, String info, Context context) {
        System.out.printf("[%s] %s%n", header, info);
        if (context != null) {
            JsonObject data = context.getData();
            for (String key : data.keySet()) {
                if (key.equals("messages") || key.equals("tools")) {
                    System.out.printf("  %s%n", key);
                    JsonArray values = data.getAsJsonArray(key);
                    for (JsonElement value : values) {
                        System.out.printf("         %s%n", value);
                    }
                }
            }
        }
        System.out.println();
    }

    public static void printResponseData(String header, String info, JsonObject response) {
        System.out.printf("[%s] %s%n", header, info);
        if (response != null) {
            for (String key : response.keySet()) {
                if (key.equals("message")) {
                    System.out.printf("  %s: %s%n", key, response.get(key));
                }
            }
        }
        System.out.println();
    }

    public static void printDict(String header, String info, Map<String, Object> data) {
        System.out.printf("[%s] %s%n", header, info);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            System.out.printf("  %s: %s%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    public static void printInfo(String header, String data) {
        System.out.printf("[%s]%n", header);
        System.out.printf("  %s%n", data);
        System.out.println();
    }
}