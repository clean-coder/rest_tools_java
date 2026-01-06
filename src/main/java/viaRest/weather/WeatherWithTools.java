package viaRest.weather;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import viaRest.util.FormatUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WeatherWithTools {
    
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String URL = "http://localhost:11434/api/chat";
    
    private static JsonObject sendRequest(JsonObject data) throws Exception {
        String jsonData = gson.toJson(data);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), JsonObject.class);
    }
    
    private static String getForecast(String city) {
        String[] forecasts;
        
        switch (city) {
            case "Paris":
                forecasts = new String[]{
                    "temperature: 30 celsius",
                    "wind: 5 km/h",
                    "precipitation: 0%"
                };
                break;
            case "London":
                forecasts = new String[]{
                    "temperature: 20 celsius",
                    "wind: 20 km/h",
                    "precipitation: 80%"
                };
                break;
            case "Berlin":
                forecasts = new String[]{
                    "temperature: 15 celsius",
                    "wind: 10 km/h",
                    "precipitation: 0%"
                };
                break;
            default:
                forecasts = new String[]{};
        }
        
        return String.join("\n---\n", forecasts);
    }
    
    private static final Map<String, Function<Map<String, String>, String>> availableFunctions = new HashMap<>();
    
    static {
        availableFunctions.put("get_forecast", args -> getForecast(args.get("city")));
    }
    
    public static String chatWithTools(String modelName, String question) throws Exception {
        Context context = new Context(modelName);
        
        context.addRequestMessage(question);
        FormatUtil.printContextData("CONTEXT", "make initial request with tool definitions", context);
        
        JsonObject firstResponse = sendRequest(context.getData());
        FormatUtil.printResponseData("RESPONSE", "from LLM", firstResponse);
        
        // Check if we should call a tool for the model
        JsonElement toolCallsElement = firstResponse.getAsJsonObject("message").get("tool_calls");
        JsonArray toolCallRequests = null;
        
        if (toolCallsElement != null && !toolCallsElement.isJsonNull()) {
            toolCallRequests = toolCallsElement.getAsJsonArray();
        }
        
        if (toolCallRequests != null && toolCallRequests.size() > 0) {
            for (JsonElement toolCallElement : toolCallRequests) {
                JsonObject toolCallRequest = toolCallElement.getAsJsonObject();

                JsonObject functionObj = toolCallRequest.getAsJsonObject("function");
                String functionName = functionObj.get("name").getAsString();
                JsonObject functionArguments = functionObj.getAsJsonObject("arguments");
                
                Map<String, Object> argsMap = new HashMap<>();
                argsMap.put("function_name", functionName);
                argsMap.put("arguments", functionArguments);
                
                FormatUtil.printDict("TOOL", "Extracted tool call request", argsMap);
                
                // Check if the function is available
                if (!availableFunctions.containsKey(functionName)) {
                    FormatUtil.printInfo("TOOL", String.format("function %s not found", functionName));
                    continue;
                } else {
                    // Call the function locally
                    FormatUtil.printInfo("TOOL", 
                        String.format("calling function %s with arguments %s", functionName, functionArguments));
                    
                    Function<Map<String, String>, String> functionToCall = availableFunctions.get(functionName);
                    
                    // Convert JsonObject arguments to Map<String, String>
                    Map<String, String> argMap = new HashMap<>();
                    for (String key : functionArguments.keySet()) {
                        argMap.put(key, functionArguments.get(key).getAsString());
                    }
                    
                    String output = functionToCall.apply(argMap);
                    FormatUtil.printInfo("TOOL", String.format("function output: %s", output));
                    
                    // Add response message of model
                    context.addMessage(firstResponse.getAsJsonObject("message"));
                    
                    // Add function result
                    context.addToolMessageWithToolResults(functionName, output);
                }
            }
        }
        
        if (toolCallRequests != null && toolCallRequests.size() > 0) {
            FormatUtil.printContextData("CONTEXT", "make request with tool results", context);
            JsonObject finalResponse = sendRequest(context.getData());
            
            FormatUtil.printResponseData("RESPONSE", "", finalResponse);
            return finalResponse.getAsJsonObject("message").get("content").getAsString();
        } else {
            return "";
        }
    }
    
    public static void main(String[] args) {
        try {
            String myQuestion = "What kind of clothes do I need for a short trip to Paris?";
            String response = chatWithTools("llama3.2", myQuestion);
            System.out.println("Final LLM response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}