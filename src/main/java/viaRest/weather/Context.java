package viaRest.weather;

import com.google.gson.JsonObject;

public class Context {
    // Definition of supported tools
    private static final JsonObject FORECAST_TOOL = createForecastTool();

    private final JsonObject data;
    
    private static JsonObject createForecastTool() {
        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        
        JsonObject function = new JsonObject();
        function.addProperty("name", "get_forecast");
        function.addProperty("description", "The weather forecast for a city");
        
        JsonObject parameters = new JsonObject();
        parameters.addProperty("type", "object");
        
        JsonObject properties = new JsonObject();
        JsonObject cityProperty = new JsonObject();
        cityProperty.addProperty("type", "string");
        cityProperty.addProperty("description", "The name of the city");
        properties.add("city", cityProperty);
        
        parameters.add("properties", properties);
        parameters.add("required", com.google.gson.JsonParser.parseString("[\"city\"]"));
        
        function.add("parameters", parameters);
        tool.add("function", function);
        
        return tool;
    }
    
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