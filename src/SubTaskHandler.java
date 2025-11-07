import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/subtasks")) {
                switch (method) {
                    case "GET":
                        handleGetSubTasks(exchange);
                        break;
                    case "POST":
                        handleAddOrUpdateSubTask(exchange);
                        break;
                    default:
                        sendError(exchange, "Unsupported method for /subtasks");
                }
            } else if (path.matches(".*/subtasks/\\d+")) {
                long id = getIdFromPath(exchange);
                switch (method) {
                    case "GET":
                        handleGetSubTaskById(exchange, id);
                        break;
                    case "DELETE":
                        handleDeleteSubTask(exchange);
                        break;
                    default:
                        sendError(exchange, "Unsupported method for /subtasks/{id}");
                }
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            try {
                sendError(exchange, "Invalid ID format");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            try {
                sendError(exchange, e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (exchange.getResponseCode() == -1) {
                try {
                    exchange.sendResponseHeaders(500, 0);
                    exchange.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = taskManager.getAllSubTasks();
        sendSubTasks(exchange, subTasks);
    }

    private void handleGetSubTaskById(HttpExchange exchange, long id) throws IOException {
        SubTask subTask = taskManager.getSubTask(id);
        if (subTask == null) {
            sendNotFound(exchange);
        } else {
            sendTask(exchange, subTask);
        }
    }

    // POST /subtasks — добавление или обновление
    private void handleAddOrUpdateSubTask(HttpExchange exchange) throws IOException {
        try {
            SubTask subTask = parseSubTask(exchange);

            if (subTask.getTaskId() == null || subTask.getTaskId() == 0) {
                taskManager.addSubTask(subTask);
                sendTask(exchange, subTask);
            } else {
                taskManager.updateSubTask(subTask, subTask.getTaskId());
                sendTask(exchange, subTask);
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        } catch (TaskOverlapException | IllegalArgumentException e) {
            sendError(exchange, e.getMessage());
        } catch (JsonSyntaxException e) {
            sendError(exchange, "Invalid JSON");
        } catch (Exception e) {
            sendError(exchange, "Invalid subtask data");
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        long id = getIdFromPath(exchange);
        taskManager.removeSubTask(id);
        sendText(exchange, "Subtask deleted", 201);
    }

    private long getIdFromPath(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        return Long.parseLong(parts[2]);
    }
}
