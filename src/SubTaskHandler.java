import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGetSubTasks(exchange);
                    break;
                case "POST":
                    handleAddSubTask(exchange);
                    break;
                case "PUT":
                    handleUpdateSubTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteSubTask(exchange);
                    break;
                default:
                    sendError(exchange, "Unsupported method");
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

    private void handleAddSubTask(HttpExchange exchange) throws IOException {
        SubTask subTask = parseSubTask(exchange);
        if (taskManager.hasOverlapWithExisting(subTask)) {
            sendHasOverlaps(exchange);
            return;
        }
        taskManager.addSubTask(subTask);
        sendTask(exchange, subTask);
    }

    private void handleUpdateSubTask(HttpExchange exchange) throws IOException {
        SubTask subTask = parseSubTask(exchange);
        taskManager.updateSubTask(subTask, subTask.getTaskId());
        sendTask(exchange, subTask);
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        long id = Long.parseLong(pathParts[pathParts.length - 1]);
        taskManager.removeSubTask(id);
        sendText(exchange, "Subtask deleted", 201);
    }
}
