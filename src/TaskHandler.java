import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGetTasks(exchange);
                    break;
                case "POST":
                    handleAddTask(exchange);
                    break;
                case "PUT":
                    handleUpdateTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteTask(exchange);
                    break;
                default:
                    sendError(exchange, "Unsupported method");
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

    private void handleAddTask(HttpExchange exchange) throws IOException {
        Task task = parseTask(exchange);
        if (taskManager.hasOverlapWithExisting(task)) {
            sendHasOverlaps(exchange);
            return;
        }
        taskManager.addTask(task);
        sendTask(exchange, task);
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        Task task = parseTask(exchange);
        taskManager.updateTask(task, task.getTaskId());
        sendTask(exchange, task);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        long id = Long.parseLong(pathParts[pathParts.length - 1]);
        taskManager.deleteTask(id);
        sendText(exchange, "Task deleted", 201);
    }
}
