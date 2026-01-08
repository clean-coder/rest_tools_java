package viaRest.weather;

import com.google.gson.JsonObject;

import static viaRest.weather.Tools.createForecastTool;

public class Context {
    private static final JsonObject FORECAST_TOOL = createForecastTool();

    private final JsonObject data;
    
    public Context(String modelName) {
        this.data = new JsonObject();
        this.data.addProperty("model", modelName);
        this.data.add("messages", new com.google.gson.JsonArray());
        
        com.google.gson.JsonArray tools = new com.google.gson.JsonArray();
        tools.add(FORECAST_TOOL);
        this.data.add("tools", tools);
        this.data.addProperty("stream", false);
    }
    
    public void addMessage(JsonObject message) {
        this.data.getAsJsonArray("messages").add(message);
    }
    
    public void addRequestMessage(String question) {
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", question);
        this.data.getAsJsonArray("messages").add(message);
    }
    
    public void addToolMessageWithToolResults(String functionName, String toolResult) {
        JsonObject message = new JsonObject();
        message.addProperty("role", "tool");
        message.addProperty("content", toolResult);
        message.addProperty("tool_name", functionName);
        this.data.getAsJsonArray("messages").add(message);
    }
    
    public JsonObject getData() {
        return data;
    }
}