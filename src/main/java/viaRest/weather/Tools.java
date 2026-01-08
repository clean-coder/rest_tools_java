package viaRest.weather;

import com.google.gson.JsonObject;

public class Tools {

    // Definition of supported tools
    public static JsonObject createForecastTool() {
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

    // Implementation of supported tools
    public static String getForecast(String city) {
        String[] forecasts = switch (city) {
            case "Paris" -> new String[]{
                    "temperature: 30 celsius",
                    "wind: 5 km/h",
                    "precipitation: 0%"
            };
            case "London" -> new String[]{
                    "temperature: 20 celsius",
                    "wind: 20 km/h",
                    "precipitation: 80%"
            };
            case "Berlin" -> new String[]{
                    "temperature: 15 celsius",
                    "wind: 10 km/h",
                    "precipitation: 0%"
            };
            default -> new String[]{};
        };

        return String.join("\n---\n", forecasts);
    }
}
