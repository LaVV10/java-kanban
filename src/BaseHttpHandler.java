import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseHttpHandler implements HttpHandler {

    protected Gson gson;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }

    protected void sendText(HttpExchange exchange, String text, int status) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\": \"Not Found\"}", 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\": \"Conflict with existing task\"}", 406);
    }

    protected void sendError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\": \"" + message + "\"}", 500);
    }

    protected void sendTasks(HttpExchange exchange, List<Task> tasks) throws IOException {
        String json = gson.toJson(tasks);
        sendText(exchange, json, 200);
    }

    protected void sendSubTasks(HttpExchange exchange, List<SubTask> subTasks) throws IOException {
        String json = gson.toJson(subTasks);
        sendText(exchange, json, 200);
    }

    protected void sendEpics(HttpExchange exchange, List<Epic> epics) throws IOException {
        String json = gson.toJson(epics);
        sendText(exchange, json, 200);
    }

    protected void sendTask(HttpExchange exchange, Task task) throws IOException {
        String json = gson.toJson(task);
        sendText(exchange, json, 200);
    }

    protected Task parseTask(HttpExchange exchange) throws IOException {
        return gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Task.class);
    }

    protected SubTask parseSubTask(HttpExchange exchange) throws IOException {
        return gson.fromJson(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                SubTask.class);
    }

    protected Epic parseEpic(HttpExchange exchange) throws IOException {
        return gson.fromJson(
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), Epic.class);
    }
}
