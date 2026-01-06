package viaRest.weather;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class WeatherNoTools {
    private static final String URL = "http://localhost:11434/api/chat";
    private static final Gson gson = new Gson();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String chat(String modelName, String question) throws Exception {
        Map<String, Object> data = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "user", "content", question)
                ),
                "stream", false
        );

        String jsonBody = gson.toJson(data);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return extractChatContent(response.body());
    }

    private static String extractChatContent(String jsonResponse) {
        JsonObject jsonData = gson.fromJson(jsonResponse, JsonObject.class);
        return jsonData.getAsJsonObject("message").get("content").getAsString();
    }

    public static void main(String[] args) {
        try {
            String question = "What kind of clothes do I need for a short trip to Paris?";
            String response = chat("llama3.2", question);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}