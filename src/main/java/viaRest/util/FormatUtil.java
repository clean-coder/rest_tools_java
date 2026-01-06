package viaRest.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import viaRest.weather.Context;

import java.util.Map;

public class FormatUtil {

    public static void printContextData(String header, String info, Context context) {
        System.out.println(String.format("[%s] %s", header, info));
        if (context != null) {
            JsonObject data = context.getData();
            for (String key : data.keySet()) {
                if (key.equals("messages") || key.equals("tools")) {
                    System.out.println(String.format("  %s", key));
                    JsonArray values = data.getAsJsonArray(key);
                    for (JsonElement value : values) {
                        System.out.println(String.format("         %s", value));
                    }
                }
            }
        }
        System.out.println();
    }

    public static void printResponseData(String header, String info, JsonObject response) {
        System.out.println(String.format("[%s] %s", header, info));
        if (response != null) {
            for (String key : response.keySet()) {
                if (key.equals("message")) {
                    System.out.println(String.format("  %s: %s", key, response.get(key)));
                }
            }
        }
        System.out.println();
    }

    public static void printDict(String header, String info, Map<String, Object> data) {
        System.out.println(String.format("[%s] %s", header, info));
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            System.out.println(String.format("  %s: %s", entry.getKey(), entry.getValue()));
        }
        System.out.println();
    }

    public static void printInfo(String header, String data) {
        System.out.println(String.format("[%s]", header));
        System.out.println(String.format("  %s", data));
        System.out.println();
    }
}