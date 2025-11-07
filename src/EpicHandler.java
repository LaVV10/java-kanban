import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {

    private static final Pattern EPICS_ID_PATTERN = Pattern.compile("^/epics/\\d+$");
    private static final Pattern EPICS_ID_SUBTASKS_PATTERN = Pattern.compile("^/epics/\\d+/subtasks$");

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if ("/epics".equals(path)) {
                        handleGetEpics(exchange);
                    } else if (EPICS_ID_PATTERN.matcher(path).matches()) {
                        handleGetEpicById(exchange);
                    } else if (EPICS_ID_SUBTASKS_PATTERN.matcher(path).matches()) {
                        handleGetSubTasksByEpicId(exchange);
                    } else {
                        sendNotFound(exchange);
                    }
                    break;

                case "POST":
                    handleAddOrUpdateEpic(exchange);
                    break;

                case "DELETE":
                    if (EPICS_ID_PATTERN.matcher(path).matches()) {
                        handleDeleteEpic(exchange);
                    } else {
                        sendError(exchange, "Unsupported DELETE path");
                    }
                    break;

                default:
                    sendError(exchange, "Unsupported method: " + method);
            }
        } catch (NumberFormatException e) {
            try {
                sendError(exchange, "Invalid ID format");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            try {
                sendError(exchange, "Server error: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            exchange.close();
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendEpics(exchange, epics);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        long id = getIdFromPath(exchange);
        Epic epic = taskManager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            sendTask(exchange, epic);
        }
    }

    private void handleGetSubTasksByEpicId(HttpExchange exchange) throws IOException {
        long epicId = getIdFromPath(exchange);
        List<SubTask> subTasks = taskManager.getEpicSubTasks(epicId);
        if (subTasks.isEmpty()) {
            sendText(exchange, "[]", 200);
        } else {
            sendSubTasks(exchange, subTasks);
        }
    }

    private void handleAddOrUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = parseEpic(exchange);

            if (epic.getTaskId() == null || epic.getTaskId() == 0) {
                // Новый эпик
                taskManager.addEpic(epic);
                sendTask(exchange, epic);
            } else {
                // Обновление — не проверяем вручную!
                taskManager.updateEpic(epic, epic.getTaskId());
                sendTask(exchange, epic);
            }
        } catch (TaskNotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendError(exchange, "Invalid epic data: " + e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        long id = getIdFromPath(exchange);
        taskManager.deleteEpic(id);
        sendText(exchange, "Epic deleted", 201);
    }

    private long getIdFromPath(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        return Long.parseLong(parts[2]);
    }
}
