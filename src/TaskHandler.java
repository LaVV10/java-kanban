import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/tasks")) {
                switch (method) {
                    case "GET":
                        handleGetTasks(exchange);
                        break;
                    case "POST":
                        handleAddOrUpdateTask(exchange);
                        break;
                    case "DELETE":
                        handleDeleteTasks(exchange);
                        break;
                    default:
                        sendError(exchange, "Unsupported method for /tasks");
                }
            } else if (path.matches(".*/tasks/\\d+")) {
                long id = getIdFromPath(exchange);
                switch (method) {
                    case "GET":
                        handleGetTaskById(exchange, id);
                        break;
                    case "DELETE":
                        handleDeleteTask(exchange);
                        break;
                    default:
                        sendError(exchange, "Unsupported method for /tasks/{id}");
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
                throw new RuntimeException(ex);
            }
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendTasks(exchange, tasks);
    }

    private void handleGetTaskById(HttpExchange exchange, long id) throws IOException {
        Task task = taskManager.getTask(id);
        if (task == null) {
            sendNotFound(exchange);
        } else {
            sendTask(exchange, task);
        }
    }

    // POST /tasks — добавление ИЛИ обновление (upsert)
    private void handleAddOrUpdateTask(HttpExchange exchange) throws IOException {
        try {
            Task task = parseTask(exchange);
            if (task.getTaskId() == null || task.getTaskId() == 0) {
                taskManager.addTask(task);
                sendTask(exchange, task);
            } else {
                taskManager.updateTask(task, task.getTaskId());
                sendTask(exchange, task);
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        } catch (TaskOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (JsonSyntaxException e) {
            sendError(exchange, "Invalid JSON");
        } catch (Exception e) {
            sendError(exchange, "Invalid task data");
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        long id = getIdFromPath(exchange);
        taskManager.deleteTask(id);
        sendText(exchange, "Task deleted", 201);
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.clearAllTasks();
        sendText(exchange, "All tasks deleted", 201);
    }

    private long getIdFromPath(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        return Long.parseLong(parts[2]);
    }
}
